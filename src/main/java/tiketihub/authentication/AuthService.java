package tiketihub.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tiketihub.authentication.dto.EmailDTO;
import tiketihub.authentication.dto.PasswardDTO;
import tiketihub.authentication.exceptions.InvalidPasswordException;
import tiketihub.authentication.exceptions.PasswordMismatchException;
import tiketihub.authentication.exceptions.UserAlreadyExistsException;
import tiketihub.user.User;
import tiketihub.user.UserDTO;
import tiketihub.user.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;
    private User currentUser;

    public void validateAndSave(UserDTO user) {
       if (!(userRepo.existsByEmail(user.getEmail()))) {
           setCurrentUser(this.currentUser.buildUser(user));
           userRepo.save(currentUser);

           //TODO :  Send email through message broker ...
       } else {
           throw new UserAlreadyExistsException("A user with the same email already exists");
       }

    }

    public void validateAndSet(PasswardDTO passwardDTO) {
        if (passwardDTO.getPassword().contentEquals(passwardDTO.getConfirmPassword())) {
            if (passwardDTO.getPassword().length() >= 8) {
                userRepo.findByEmail(currentUser.getEmail()).
                        ifPresent(user -> user.setPassword(passwardDTO.getPassword())); //TODO : Encode password after setting up JWT security
            }
            else throw new InvalidPasswordException("The password should contain at-least 8 characters");
        }
        else throw new PasswordMismatchException("The password and confirm-password do not match");
    }

    public String validateEmailAndRequestToken(EmailDTO emailDTO) {
        if (userRepo.existsByEmail(emailDTO.getEmail())) {
            //TODO : Request password reset token
            return null;
        }
        else throw new InvalidPasswordException("The email you entered does not exist");
    }

    private void setCurrentUser(User user) {
        this.currentUser = user;
    }

}
