package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "organized_event")
public class OrganizedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String organized_event_id;

    @ManyToMany(targetEntity = User.class)
    private List<User> users;
    @ManyToMany
    @JoinTable(
            name = "organizedevent_event",
            joinColumns = @JoinColumn(name = "organized_event_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
    @ManyToOne(targetEntity = Role.class)
    private Role role;
    @ManyToMany(targetEntity = Token.class)
    private List<Token> tokens;
}
