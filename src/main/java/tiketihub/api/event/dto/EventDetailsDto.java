package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.api.event.exceptions.AccessDeniedException;
import tiketihub.api.event.exceptions.NoHostFoundException;
import tiketihub.api.event.model.Attendee;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.Organizer;
import tiketihub.user.User;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class EventDetailsDto {
    private UUID eventId;
    private UUID organizerId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long duration;
    private Float pricing;
    private String venue;
    private Long capacity;
    private Long ageRestriction;
    private String description;
    private CategoryDto category;

    public EventDetailsDto(Event event) {
        this.eventId = event.getId();
        this.organizerId = event.getOrganizer().getId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.duration = event.getDuration();
        this.pricing = event.getPricing();
        this.venue = event.getVenue();
        this.capacity = event.getCapacity();
        this.ageRestriction = event.getAgeRestriction();
        this.description = event.getDescription();
        this.category = CategoryDto.CategoryToCategoryDtoConversion(event.getCategory());
    }

    public static EventUserAccessLevelDto hostUser(Event event) {
        Organizer organizer = event.getOrganizer();
        User user = organizer.getUser();
        EventUserAccessLevelDto host = new EventUserAccessLevelDto();
        host.setUserId(user.getId());
        host.setUsername(user.getFirstName(),user.getLastName());
        host.setEmail(user.getEmail());
        return host;
    }
    public static EventUserAccessLevelDto coHostUser(Event event) {
        for (Attendee attendee : event.getAttendees()) {
            if (attendee.getRole().contentEquals("CO-HOST")) {
                User user = attendee.getUser();
                EventUserAccessLevelDto host = new EventUserAccessLevelDto();
                host.setUserId(user.getId());
                host.setUsername(user.getFirstName(),user.getLastName());
                host.setEmail(user.getEmail());
                return host;
            }
        }
        throw new NoHostFoundException("Only hosts/co-host can make changes to events");
    }
    public static EventUserAccessLevelDto guestUser(Event event) {
        for (Attendee attendee : event.getAttendees()) {
            if (attendee.getRole().contentEquals("GUEST")) {
                User user = attendee.getUser();
                EventUserAccessLevelDto host = new EventUserAccessLevelDto();
                host.setUserId(user.getId());
                host.setUsername(user.getFirstName(),user.getLastName());
                host.setEmail(user.getEmail());
                return host;
            }
        }
        throw new AccessDeniedException("Only hosts/co-host can make changes to events");
    }

}
