package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private String Email;
    private String password;
    private String recoveryEmail;
    private String dateOfBirth;
    private Gender gender;

    public enum Gender {
        MALE,FEMALE
    }
}
