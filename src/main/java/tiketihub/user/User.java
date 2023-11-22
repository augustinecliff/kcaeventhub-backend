package tiketihub.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Entity
@Data
@Table(name = "User" ,
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 22233301L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Email
    private String email;
    private String password;
    private String phoneNumber;
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    @NotNull
    private String gender;
    @NotNull
    private long age;
    @NotNull
    private String userRole;

    public static User buildUser(UserDTO user) {
        User user1 = new User();
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setEmail(user.getEmail());
        user1.setPhoneNumber(user.getPhoneNumber());
        user1.setDateOfBirth(user.getDateOfBirth());
        user1.setGender(user.getGender());
        user1.setUserRole("ROLE_USER");
        return user1;
    }

    public static UserDTO UserTouserDTOConverter(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setGender(user.getGender());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDateOfBirth(user.getDateOfBirth());

        return userDTO;
    }
}
