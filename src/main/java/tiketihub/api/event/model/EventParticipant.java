package tiketihub.api.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tiketihub.api.event.dto.AddParticipantDto;
import tiketihub.user.User;

import java.util.UUID;

@Getter
@Setter
@Entity
public class EventParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String role;

    @ManyToOne(targetEntity = User.class)
    private User user;


    public static EventParticipant addParticipant(AddParticipantDto participantDto) {
        EventParticipant participant = new EventParticipant();
        participant.setRole(participantDto.getRole());
        participant.setUser(participantDto.getUser());
        return participant;
    }
}