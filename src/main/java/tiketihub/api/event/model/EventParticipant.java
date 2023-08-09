package tiketihub.api.event.model;

import jakarta.persistence.*;
import lombok.Data;
import tiketihub.user.User;

import java.util.Optional;
import java.util.UUID;

@Data
@Entity
public class EventParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String role;

    @OneToOne(targetEntity = User.class)
    private User user;

}
// payment will be attached here.