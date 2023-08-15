package tiketihub.api.event.model;

import jakarta.persistence.*;
import lombok.Data;
import tiketihub.api.event.dto.EventParticipantDto;
import tiketihub.user.User;

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


    public static EventParticipant addParticipant(EventParticipantDto participantDto) {
        EventParticipant participant = new EventParticipant();
        participant.setRole(participantDto.getRole());
        participant.setUser(participantDto.getUser());
        return participant;
    }
}