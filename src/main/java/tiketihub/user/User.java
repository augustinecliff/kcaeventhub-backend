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
@Table(name = "User_Table" ,
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
        user1.setDateOfBirth(user.getDateOfBirth());
        user1.setGender(user.getGender());
        user1.setAge(
                Period.between(
                        user.getDateOfBirth(),
                        LocalDate.now()
                ).getYears()
        );
        user1.setUserRole("ROLE_USER");
        return user1;
    }
}
