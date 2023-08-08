package tiketihub.user;

import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@Data
public class UserDTO extends User{
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;

}
