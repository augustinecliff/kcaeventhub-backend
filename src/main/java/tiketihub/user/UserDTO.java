package tiketihub.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDTO{
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;

}
