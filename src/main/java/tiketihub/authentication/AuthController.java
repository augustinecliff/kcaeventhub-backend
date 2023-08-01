package tiketihub.authentication;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tiketihub.authentication.dto.EmailDTO;
import tiketihub.authentication.dto.LoginDTO;
import tiketihub.authentication.dto.PasswardDTO;
import tiketihub.authentication.dto.TokenDTO;
import tiketihub.authentication.exceptions.InvalidEmailException;
import tiketihub.authentication.exceptions.UserAlreadyExistsException;
import tiketihub.authentication.response.AuthResponse;
import tiketihub.user.UserDTO;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private final AuthService authService;
    private AuthResponse response;
    private TokenDTO tokenDTO;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO credential){
        log.info("\nlogin process: (Email: " + credential.getEmail()+"| Password: "+ credential.getPassword() + ")");
        return null;//TODO : it is to return an authentication token
    }
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        return null;//TODO: invalidate authentication token
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<TokenDTO> forgotPassword(@RequestBody EmailDTO emailDTO) {
        try {
            String resetToken = authService.validateEmailAndRequestToken(emailDTO);
            tokenDTO = new TokenDTO();
            tokenDTO.setAuthResponse(new AuthResponse("Created", "A password reset token has been sent to " + emailDTO.getEmail()));
            tokenDTO.setToken(resetToken);//TODO : configure jwt and create a method that sets token.
            return ResponseEntity.status(HttpStatus.CREATED).body(tokenDTO);
        }
        catch (InvalidEmailException exc) {
            tokenDTO.setAuthResponse(new AuthResponse("Not Found",exc.getMessage()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tokenDTO);
        }
    }

/*catch (InvalidTokenException exc) {//TODO : configure token exception class (to be used in all methods that require it)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.Please request for a new one");
        }*/

    @PostMapping("/set-password")
    public ResponseEntity<AuthResponse> setPassword(@RequestBody PasswardDTO passwardDTO){//TODO : configure how to receive password set or reset token
        try {
            authService.validateAndSet(passwardDTO);
            response = new AuthResponse("Created","User has been created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (Exception exc) {
            response = new AuthResponse("Conflict", exc.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthResponse> processRegistration(@RequestBody UserDTO user){

        try {
            authService.validateAndSave(user);
            response = new AuthResponse("Ok","A verification email has been sent to " + user.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch(UserAlreadyExistsException exc) {
            response = new AuthResponse("Conflict", exc.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

}
