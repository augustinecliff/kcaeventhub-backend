package tiketihub.api.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String title;
    @NotNull
    private String date;
    @NotNull
    private String duration;
    @NotNull
    private String pricing;
    @NotNull
    private String venue;
    @NotNull
    private String capacity;
    @NotNull
    private String ageRestriction;
    @NotNull
    private String description;

    @ManyToOne(targetEntity = Category.class)
    private Category category;

    @ManyToOne(targetEntity = EventParticipant.class)
    private EventParticipant participant;


}
