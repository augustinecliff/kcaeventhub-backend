package tiketihub.api.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import tiketihub.api.event.dto.CategoryDto;
import tiketihub.api.event.dto.CreateEventDto;
import tiketihub.api.event.dto.EditEventDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {("title")})})
public class Event {
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
    private String description;
    @NotNull
    private boolean active;

    @ManyToOne(targetEntity = Category.class)
    private Category category;
    @ManyToMany
    @JoinTable(name = "Event_Attendees",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "attendee_id"))
    private Set<Attendee> attendees = new HashSet<>();

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
