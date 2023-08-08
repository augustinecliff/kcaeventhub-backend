package tiketihub.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tiketihub.authentication.dto.EmailDTO;
import tiketihub.authentication.dto.LoginDTO;
import tiketihub.authentication.dto.PasswardDTO;
import tiketihub.authentication.exceptions.*;
import tiketihub.emailconfig.EmailConfig;
import tiketihub.authentication.security.JWTUtil;
import tiketihub.user.User;
import tiketihub.user.UserDTO;
import tiketihub.user.UserRepository;

import java.util.Date;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EmailConfig email;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private  AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void processLogout(String authToken) {
        authToken = authToken.replace("Bearer ","");
        log.info(authToken);
        jwtUtil.BlackListToken(authToken);
    }

    public String validateAndGenerateAuthToken(LoginDTO credential) {
        if (userRepo.existsByEmail(credential.getEmail())) {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credential.getEmail(),credential.getPassword())
            );
            return jwtUtil.generateJwtAuthenticationToken(auth);
        }
        else throw new InvalidUserCredentialException("Either the password or email is invalid!");
    }


    public String validateUserAndGenerateSetPasswordToken(UserDTO user) {
       if (!(userRepo.existsByEmail(user.getEmail()))) {
           User currentUser = User.buildUser(user);
           userRepo.save(currentUser);


           //TODO :  Send email through 'message broker' ... (for setting the password)
           String token = jwtUtil.generatePasswordConfigToken(currentUser.getEmail(),
                   new Date(new Date().getTime() + 1800000));// 30 mins
           email.sendSimpleEmail(currentUser.getEmail(),"Configure account password" , token);
           return token;

       } else {
           throw new UserAlreadyExistsException("A user with the same email already exists");
       }
    }

    public void validateAndSet(PasswardDTO passwardDTO,String token) {
        if (jwtUtil.validateToken(token)) {
            log.info("\npassword: " + passwardDTO.getPassword()+
                    "\nconfirmpassword: "+ passwardDTO.getConfirmPassword());
        if (passwardDTO.getPassword() != null &&
                passwardDTO.getConfirmPassword() != null &&
                passwardDTO.getPassword().contentEquals(passwardDTO.getConfirmPassword())) {
            if (passwardDTO.getPassword().length() >= 8) {
               String email = jwtUtil.getEmailFromToken(token);
               userRepo.findByEmail(email).
                        ifPresent(user -> {
                            user.setPassword(passwordEncoder.encode(passwardDTO.getPassword()));
                            userRepo.save(user);
                        });
            }
            else throw new InvalidPasswordException("The password should contain at-least 8 characters");
        }
        else throw new PasswordMismatchException("The password and confirm-password do not match");
        }
        else throw new InvalidTokenException("The token used is invalid");
    }

    public String validateEmailAndGenerateResetPasswordToken(EmailDTO emailDTO) {
        if (userRepo.existsByEmail(emailDTO.getEmail())) {
            String token = jwtUtil.
                    generatePasswordConfigToken(
                            emailDTO.getEmail(),
                            new Date(new Date().getTime()+1800000));// 30 mins
            email.sendSimpleEmail(emailDTO.getEmail(),"Forgot password" , token);

            return token;
        }
        else throw new InvalidEmailException("The email you entered does not exist");
    }


}
