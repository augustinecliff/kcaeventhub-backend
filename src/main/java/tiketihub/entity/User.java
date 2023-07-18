package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_table")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID user_id;
    @ManyToMany
    @JoinTable(
            name = "user_email",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "email_id"))
    private List<Email> emails;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private LocalDate dateOfBirth;
    private Gender gender;

    public enum Gender {
        MALE,FEMALE,OTHER
    }

    public String toString() {
        return "\n...................................\n"
                +"firstName        :" + firstName + "\n"
                +"lastName         :" + lastName + "\n"
                +"username         :" + username + "\n"
                +"password         :" + password + "\n"
                +"confirm password :" + confirmPassword + "\n"
                +"Email            :" + email + "\n"
                +"dateOfBirth      :" + dateOfBirth + "\n"
                +"gender           :" + gender + ".\n"
                +".................................\n";
    }
    public void encodePassword(PasswordEncoder encoder) {


        this.password =  encoder.encode(password);
    }


}
