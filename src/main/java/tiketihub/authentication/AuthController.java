package tiketihub.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tiketihub.authentication.dto.EmailDTO;
import tiketihub.authentication.dto.LoginDTO;
import tiketihub.authentication.dto.PasswardDTO;
import tiketihub.authentication.dto.TokenDTO;
import tiketihub.authentication.exceptions.InvalidEmailException;
import tiketihub.authentication.exceptions.InvalidUserCredentialException;
import tiketihub.authentication.exceptions.UserAlreadyExistsException;
import tiketihub.authentication.response.AuthResponse;
import tiketihub.authentication.security.JWTUtil;
import tiketihub.user.UserDTO;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private final AuthService authService;
    @Autowired
    private JWTUtil jwtUtil;
    private AuthResponse response;
    private TokenDTO tokenDTO;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/authenticatedUser")
   /* @PreAuthorize("hasRole('USER')")*/
    public ResponseEntity<AuthResponse> sendingEmail() {
        try {
            response = new AuthResponse("Ok",
                    "Yes user is authenticated");
            log.info("\nstatus => " + response.getStatus()+" :: msg => "+ response.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            response = new AuthResponse("Conflict",
                    "User is not Authenticated");
            log.info("\nstatus => " + response.getStatus()+" :: msg => "+ response.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO credential){
        try {
            String generatedToken = authService.validateAndGenerateAuthToken(credential);
            tokenDTO = new TokenDTO();
            response = new AuthResponse("Accepted","Authentication Successful");
            tokenDTO.setAuthResponse(response);
            tokenDTO.setPurpose("Authentication and Authorization Token");
            tokenDTO.setToken(generatedToken);
            log.info("\nlogin process: (Email: " + credential.getEmail()+
                    "| Password: "+ credential.getPassword() + ")");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenDTO);
        }
        catch (InvalidUserCredentialException exc) {
            tokenDTO = new TokenDTO();
            response = new AuthResponse("Unauthorized", exc.getMessage());
            tokenDTO.setAuthResponse(response);
            tokenDTO.setPurpose("Authentication and Authorization Token");
            tokenDTO.setToken("");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenDTO);
        }

    }
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String authToken) {
        authToken = (authToken == null) ? "" : authToken.replace("Bearer ","");

            if (jwtUtil.validateToken(authToken)) {
                if (!(jwtUtil.isTokenBlackListed(authToken))) {
                    authService.processLogout(authToken);
                    response = new AuthResponse("Ok", "Logout successful");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                else {
                    response = new AuthResponse("Not_Found", "No user session found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }
            else {
                response = new AuthResponse("Conflict", "Invalid Authorization token");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        }
    @PostMapping("/forgot-password")
    public ResponseEntity<TokenDTO> forgotPassword(@RequestBody EmailDTO emailDTO) {
        try {
            String resetToken = authService.validateEmailAndGenerateResetPasswordToken(emailDTO);
            tokenDTO = new TokenDTO();
            tokenDTO.setAuthResponse(new AuthResponse("Created",
                    "A password reset token has been sent to " + emailDTO.getEmail()));
            tokenDTO.setToken(resetToken);
            tokenDTO.setPurpose("Password reset token");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(tokenDTO);
        }
        catch (InvalidEmailException exc) {
            //Security hides true response when the email not found
            tokenDTO.setAuthResponse(new AuthResponse("Not_Found",
                    exc.getMessage()));
            log.info(tokenDTO.toString());

            tokenDTO.setAuthResponse(new AuthResponse("Ok",
                    "A password reset token has been sent to " + emailDTO.getEmail()));
            return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
        }
    }

    @PatchMapping("/set-password")
    public ResponseEntity<TokenDTO> setPassword(@RequestBody PasswardDTO passwardDTO,
                                                    @RequestParam("token") String token){
        try {
            authService.validateAndSet(passwardDTO, token);
            tokenDTO = new TokenDTO();
            tokenDTO.setToken("");
            tokenDTO.setAuthResponse(new AuthResponse("Accepted",
                    "User credentials have been updated successfully"));
            tokenDTO.setPurpose("Set password");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenDTO);
        }
        catch (Exception exc) {
            response = new AuthResponse("Conflict", exc.getMessage());
            tokenDTO = new TokenDTO();
            tokenDTO.setAuthResponse(response);
            tokenDTO.setToken("");
            tokenDTO.setPurpose("Set password");
            log.info(tokenDTO.toString());
            exc.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tokenDTO);
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<TokenDTO> processRegistration(@RequestBody UserDTO user){

        try {
            String setPasswordToken = authService.validateUserAndGenerateSetPasswordToken(user);
            tokenDTO = new TokenDTO();
            tokenDTO.setAuthResponse(new AuthResponse("Accepted",
                    "A password configuration token has been sent to " + user.getEmail()));
            tokenDTO.setToken(setPasswordToken);
            tokenDTO.setPurpose("Password configuration token");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenDTO);
        }
        catch(UserAlreadyExistsException exc) {
            tokenDTO = new TokenDTO();
            tokenDTO.setAuthResponse(new AuthResponse("Conflict", exc.getMessage()));
            tokenDTO.setToken("");
            tokenDTO.setPurpose("Password configuration token");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tokenDTO);
        }
    }

}
