package tiketihub.authentication;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tiketihub.authentication.dto.LoginDTO;
import tiketihub.authentication.dto.PasswardDTO;
import tiketihub.authentication.exceptions.InvalidPasswordException;
import tiketihub.authentication.exceptions.InvalidTokenException;
import tiketihub.authentication.exceptions.InvalidUserCredentialException;
import tiketihub.authentication.exceptions.PasswordMismatchException;
import tiketihub.authentication.security.jwt.JWTUtil;
import tiketihub.emailconfig.EmailConfig;
import tiketihub.emailconfig.templates.EmailTemplates;
import tiketihub.user.UserRepository;

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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authManager;

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


    /*public String validateUserAndGenerateSetPasswordToken(UserDTO user) {
       if (!(userRepo.existsByEmail(user.getEmail()))) {
           User currentUser = User.buildUser(user);
           userRepo.save(currentUser);

           //TODO :  Send email through 'message broker' ... (for setting the password)
           String token = jwtUtil.generatePasswordConfigToken(
                   new JwtDTO(
                   String.valueOf(currentUser.getId()),
                   currentUser.getEmail()
           ),
                   new Date(new Date().getTime() + 1800000));// 30 mins
           email.sendSimpleEmail(currentUser.getEmail(),"Configure account password" , token);
           return token;

       } else {
           throw new UserAlreadyExistsException("A user with the same email already exists");
       }
    }*/

    public void validateAndSet(PasswardDTO passwardDTO,String token) { // to be removed
        if (jwtUtil.validateToken(token)) {
            log.info("\npassword: " + passwardDTO.getPassword()+
                    "\nconfirmPassword: "+ passwardDTO.getConfirmPassword());
        if (passwardDTO.getPassword() != null &&
                passwardDTO.getConfirmPassword() != null &&
                passwardDTO.getPassword().contentEquals(passwardDTO.getConfirmPassword())) {
            if (passwardDTO.getPassword().length() >= 8) {
               String userEmail = jwtUtil.getUserIdAndEmailFromToken(token).getEmail();
               userRepo.findByEmail(userEmail).
                        ifPresent(user -> {
                            user.setPassword(passwordEncoder.encode(passwardDTO.getPassword()));
                            userRepo.save(user);
                            EmailTemplates template = new EmailTemplates();
                            try {
                                email.sendMailWithAttachment(userEmail,"New account registration",template.getNewUser());
                            } catch (MessagingException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
            else throw new InvalidPasswordException("The password should contain at-least 8 characters");
        }
        else throw new PasswordMismatchException("The password and confirm-password do not match");
        }
        else throw new InvalidTokenException("The token used is invalid");
    }

   /* public String validateEmailAndGenerateResetPasswordToken(EmailDTO emailDTO) { // To be removed.
        if (userRepo.existsByEmail(emailDTO.getEmail())) {
            String token = jwtUtil.
                    generatePasswordConfigToken(
                            new JwtDTO(
                                    null,
                                    emailDTO.getEmail()
                            ),
                            new Date(new Date().getTime()+1800000));// 30 mins
            email.sendSimpleEmail(emailDTO.getEmail(), "Forgot password" , token);

            return token;
        }
        else throw new InvalidEmailException("The email you entered does not exist");
    }*/
}
