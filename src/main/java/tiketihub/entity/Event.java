package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID event_id;
    @ManyToOne
    private EventCategory categories;
    private String event_title;
    private Date event_Date;
    private String event_duration;
    private String pricing;
    private String event_venue;
    private Long event_capacity;
    private String event_sponsors;
    private String age_restriction;
    private String event_description;
}
