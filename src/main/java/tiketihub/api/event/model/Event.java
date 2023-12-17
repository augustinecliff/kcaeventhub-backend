package tiketihub.api.event.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import tiketihub.api.event.dto.CategoryDto;
import tiketihub.api.event.dto.CreateEventDto;
import tiketihub.api.event.dto.EditEventDto;

import java.io.Serial;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "event", uniqueConstraints = {@UniqueConstraint(columnNames = {("title")})})
public class Event {
    @Serial
    private static final long serialVersionUID = 44432301L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    @NotNull
    private Long duration;
    @NotNull
    private Float pricing;
    @NotNull
    private String venue;
    @NotNull
    private Long capacity;
    @NotNull
    private Long ageRestriction;
    @NotNull
    @Column(length = 4000)
    private String description;
    @NotNull
    private boolean active;

    @ManyToOne(targetEntity = Category.class)
    private Category category;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "Event_Attendees",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "attendee_id"))
    private List<Attendee> attendees = new LinkedList<>();

    @ManyToOne(targetEntity = Organizer.class)
    private Organizer organizer;

    public Event(String title, LocalDate startDate,
                 LocalDate endDate, Long duration,
                 float pricing, String venue, Long capacity,
                 Long ageRestriction, String description) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.pricing = pricing;
        this.venue = venue;
        this.capacity = capacity;
        this.ageRestriction = ageRestriction;
        this.description = description;
    }
    public Event(String title, LocalDate startDate,
                 LocalDate endDate, Long duration,
                 float pricing, String venue, Long capacity,
                 Long ageRestriction, String description,Category category) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.pricing = pricing;
        this.venue = venue;
        this.capacity = capacity;
        this.ageRestriction = ageRestriction;
        this.description = description;
        this.category = category;
    }

    public static Event buildEvent(CreateEventDto eventDto) {
        return new Event(
                eventDto.getTitle(),
                eventDto.getStartDate(),
                eventDto.getEndDate(),
                eventDto.getDuration(),
                eventDto.getPricing(),
                eventDto.getVenue(),
                eventDto.getCapacity(),
                eventDto.getAgeRestriction(),
                eventDto.getDescription()
        );
    }
    public void editEvent(EditEventDto changes) {
        new Event(
                this.title = changes.getTitle(),
                this.startDate = changes.getStartDate(),
                this.endDate = changes.getEndDate(),
                this.duration = changes.getDuration(),
                this.pricing = changes.getPricing(),
                this.venue = changes.getVenue(),
                this.capacity = changes.getCapacity(),
                this.ageRestriction = changes.getAgeRestriction(),
                this.description = changes.getDescription(),
                this.category = CategoryDto.categoryDtoToCategoryConversion(changes.getCategory())
        );
    }
}
