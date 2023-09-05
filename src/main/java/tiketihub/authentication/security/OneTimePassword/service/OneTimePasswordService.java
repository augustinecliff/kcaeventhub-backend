package tiketihub.authentication.security.OneTimePassword.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tiketihub.authentication.dto.EmailDTO;
import tiketihub.authentication.exceptions.InvalidEmailException;
import tiketihub.authentication.exceptions.InvalidTokenException;
import tiketihub.authentication.exceptions.UserAlreadyExistsException;
import tiketihub.authentication.security.OneTimePassword.dto.CodeDTO;
import tiketihub.authentication.security.OneTimePassword.dto.OneTimePasswordDTO;
import tiketihub.authentication.security.dto.JwtDTO;
import tiketihub.authentication.security.jwt.JWTUtil;
import tiketihub.emailconfig.EmailConfig;
import tiketihub.user.User;
import tiketihub.user.UserDTO;
import tiketihub.user.UserRepository;

import java.util.Date;
import java.util.Random;

@Service
public class OneTimePasswordService {
    @Autowired
    private EmailConfig email;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JWTUtil jwt;
    public String validateEmailAndGenerateOTP(EmailDTO emailDTO) {
        User user = userRepo.findByEmail(emailDTO.getEmail())
                .orElseThrow(() -> new InvalidEmailException("The email you entered does not exist"));
        OneTimePasswordDTO code = new OneTimePasswordDTO(
                user.getId(),
                new Random().nextInt(1000, 10_000)
        );
        email.sendSimpleEmail(emailDTO.getEmail(), "Forgot password" , String.valueOf(code.getCode()));

        return jwt.convertOTPtoToken(code);
    }
    public String validateOTPAndGenerateToken(CodeDTO code, String OTPtoken) {
        if (jwt.validateToken(OTPtoken)) {
            if (code.getCode() == jwt.getOTP(OTPtoken).getCode()) {
                User user = userRepo.findById(jwt.getOTP(OTPtoken).getUserId())
                        .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
                return jwt.generatePasswordConfigToken(
                        new JwtDTO(user.getId().toString(),user.getEmail()),
                        new Date(new Date().getTime()+180000)
                );
            }
        }
        throw new InvalidTokenException("The token is invalid");
    }
    public String validateUserAndGenerateSetPasswordToken(UserDTO userDTO) {
        if (!(userRepo.existsByEmail(userDTO.getEmail()))) {
            User user = User.buildUser(userDTO);
            userRepo.save(user);

            //TODO :  Send email through 'message broker' ... (for setting the password)
            OneTimePasswordDTO code = new OneTimePasswordDTO(
                    user.getId(),
                    new Random().nextInt(1000, 10_000)
            );
            email.sendSimpleEmail(userDTO.getEmail(), "Forgot password" , String.valueOf(code.getCode()));
            return jwt.convertOTPtoToken(code);

        } else {
            throw new UserAlreadyExistsException("A user with the same email already exists");
        }
    }
}
