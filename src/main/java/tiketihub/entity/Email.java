package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "email")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String email_id;

    @ManyToMany(mappedBy = "emails")
    private List<User> user;

    @ManyToMany(mappedBy = "emails")
    private List<Ticket> ticket;
    private String subject;
    private String Content;
    private String Receipt;
    private String Status;
}
