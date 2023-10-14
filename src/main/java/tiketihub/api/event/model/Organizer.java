package tiketihub.api.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tiketihub.api.event.dto.AddParticipantDto;
import tiketihub.user.User;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Organizer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(targetEntity = User.class)
    private User user;

    @OneToMany(mappedBy = "organizer")
    Set<Event> events;

    public static Organizer addOrganizer(AddParticipantDto participantDto) {
        Organizer organizer = new Organizer();
        organizer.setUser(participantDto.getUser());
        return organizer;
    }
}
