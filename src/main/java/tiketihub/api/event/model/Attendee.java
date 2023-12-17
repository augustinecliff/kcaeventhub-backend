package tiketihub.api.event.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tiketihub.api.event.dto.AddParticipantDto;
import tiketihub.user.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Attendee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @ManyToOne(targetEntity = User.class)
    private User user;
    private String role;

    @ManyToMany(mappedBy = "attendees")
    @JsonIgnore
    private List<Event> events;

    public static Attendee addAttendee(AddParticipantDto participantDto) {
        Attendee participant = new Attendee();
        participant.setRole(participantDto.getRole());
        participant.setUser(participantDto.getUser());
        return participant;
    }

}
