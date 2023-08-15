package tiketihub.api.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tiketihub.authentication.exceptions.InvalidTokenException;
import tiketihub.authentication.security.jwt.JWTUtil;
import tiketihub.user.User;
import tiketihub.user.UserDTO;
import tiketihub.user.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
@Slf4j
public class ProfileService {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    public UserDTO validateTokenAndFetchProfile(String authToken) {
        authToken = authToken.replace("Bearer ", "");
            if (jwtUtil.validateToken(authToken)) {
                UUID userId = UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(authToken).getUserId());

                User user = userRepo.findById(userId).
                        orElseThrow(() ->
                                new UsernameNotFoundException("The user does not exist"));

                return User.UserTouserDTOConverter(user);
            }
            else throw new InvalidTokenException("The token is invalid");
    }

    public UserDTO validateTokenAndEditProfile(String authToken, UserDTO userDTO) {
        authToken = authToken.replace("Bearer ", "");
        if (jwtUtil.validateToken(authToken)) {
            UUID userId = UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(authToken).getUserId());

            User user = userRepo.findById(userId).
                    orElseThrow(() ->
                            new UsernameNotFoundException("The user does not exist"));
            if (userDTO.getEmail().contentEquals(user.getEmail())) {
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setDateOfBirth(userDTO.getDateOfBirth());
                user.setGender(userDTO.getGender());
                user.setAge((Period.between(user.getDateOfBirth(), LocalDate.now())).getYears());
                userRepo.save(user);
            } else {
                throw new RuntimeException("Email cannot be changed");
            }

            return User.UserTouserDTOConverter(user);
        }
        else throw new InvalidTokenException("The token is invalid");
    }

}

