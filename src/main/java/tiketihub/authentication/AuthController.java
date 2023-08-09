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
    private TokenDTO tokenDTO;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse<TokenDTO>> login(@RequestBody LoginDTO credential){
        try {
            String generatedToken = authService.validateAndGenerateAuthToken(credential);
            tokenDTO = new TokenDTO();
            tokenDTO.setPurpose("Authentication and Authorization Token");
            tokenDTO.setToken(generatedToken);

            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.OK,
                    "Authentication Successful",
                    tokenDTO
                    );
            log.info("\nlogin process: (Email: " + credential.getEmail()+
                    "| Password: "+ credential.getPassword() + ")");
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (InvalidUserCredentialException exc) {
            tokenDTO = new TokenDTO();

            tokenDTO.setPurpose("Authentication and Authorization Token");
            tokenDTO.setToken("");
            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.UNAUTHORIZED,
                    exc.getMessage(),
                    tokenDTO
            );
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }
    @PostMapping("/logout")
    public <T> ResponseEntity<AuthResponse<T>> logout(@RequestHeader("Authorization") String authToken) {
        authToken = (authToken == null) ? "" : authToken.replace("Bearer ","");

            if (jwtUtil.validateToken(authToken)) {
                if (!(jwtUtil.isTokenBlackListed(authToken))) {
                    authService.processLogout(authToken);
                    AuthResponse<T> response = new AuthResponse<>(
                            HttpStatus.OK,
                            "Logout successful",
                            null
                    );
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                else {
                    AuthResponse<T> response = new AuthResponse<>(
                            HttpStatus.NOT_FOUND,
                            "No user session found",
                            null
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }
            else {
                AuthResponse<T> response = new AuthResponse<>(
                        HttpStatus.CONFLICT,
                        "Invalid Authorization token",
                        null
                );
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

        }
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse<TokenDTO>> forgotPassword(@RequestBody EmailDTO emailDTO) {
        try {
            String resetToken = authService.validateEmailAndGenerateResetPasswordToken(emailDTO);
            tokenDTO = new TokenDTO();
            tokenDTO.setToken(resetToken);
            tokenDTO.setPurpose("Password reset token");
            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.CREATED,
                    "A password reset token has been sent to " + emailDTO.getEmail(),
                    tokenDTO
            );
            log.info(tokenDTO.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (InvalidEmailException exc) {
            //Security hides true response when the email not found
            tokenDTO = new TokenDTO();
            tokenDTO.setToken("");
            tokenDTO.setPurpose("Password reset token");
            log.info(tokenDTO.toString());

            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.CREATED,
                    "A password reset token has been sent to " + emailDTO.getEmail(),
                    tokenDTO
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PatchMapping("/set-password")
    public ResponseEntity<AuthResponse<TokenDTO>> setPassword(@RequestBody PasswardDTO passwardDTO,
                                                    @RequestParam("token") String token){
        try {
            authService.validateAndSet(passwardDTO, token);
            tokenDTO = new TokenDTO();
            tokenDTO.setToken("");
            tokenDTO.setPurpose("Set password");
            log.info(tokenDTO.toString());
            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.ACCEPTED,
                    "User credentials have been updated successfully",
                    tokenDTO
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        catch (Exception exc) {
            tokenDTO = new TokenDTO();
            tokenDTO.setToken("");
            tokenDTO.setPurpose("Set password");
            log.info(tokenDTO.toString());
            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.CONFLICT,
                    exc.getMessage(),
                    tokenDTO
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponse<TokenDTO>> processRegistration(@RequestBody UserDTO user){

        try {
            String setPasswordToken = authService.validateUserAndGenerateSetPasswordToken(user);
            tokenDTO = new TokenDTO();
            tokenDTO.setToken(setPasswordToken);
            tokenDTO.setPurpose("Password configuration token");
            log.info(tokenDTO.toString());
            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.ACCEPTED,
                    "A password configuration token has been sent to " + user.getEmail(),
                    tokenDTO
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        catch(UserAlreadyExistsException exc) {
            tokenDTO = new TokenDTO();
            tokenDTO.setToken("");
            tokenDTO.setPurpose("Password configuration token");
            log.info(tokenDTO.toString());
            AuthResponse<TokenDTO> response = new AuthResponse<>(
                    HttpStatus.CONFLICT,
                    exc.getMessage(),
                    tokenDTO
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

}
