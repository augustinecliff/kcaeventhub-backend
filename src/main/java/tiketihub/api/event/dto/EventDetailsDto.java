package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.api.event.exceptions.NoHostFoundException;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.EventParticipant;
import tiketihub.user.User;

import java.time.LocalDate;

@Data
public class EventDetailsDto {
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
        for (EventParticipant participant : event.getParticipants()) {
            if (participant.getRole().contentEquals("HOST")) {
                User user = participant.getUser();
                EventUserAccessLevelDto host = new EventUserAccessLevelDto();
                host.setUserId(user.getId());
                host.setUsername(user.getFirstName(),user.getLastName());
                host.setEmail(user.getEmail());
                return host;
            }
        }
        throw new NoHostFoundException("No host found in this event!");
    }
    public static EventUserAccessLevelDto coHostUser(Event event) {
        for (EventParticipant participant : event.getParticipants()) {
            if (participant.getRole().contentEquals("CO-HOST")) {
                User user = participant.getUser();
                EventUserAccessLevelDto host = new EventUserAccessLevelDto();
                host.setUserId(user.getId());
                host.setUsername(user.getFirstName(),user.getLastName());
                host.setEmail(user.getEmail());
                return host;
            }
        }
        throw new NoHostFoundException("No host found in this event!");
    }
    public static EventUserAccessLevelDto guestUser(Event event) {
        for (EventParticipant participant : event.getParticipants()) {
            if (participant.getRole().contentEquals("GUEST")) {
                User user = participant.getUser();
                EventUserAccessLevelDto host = new EventUserAccessLevelDto();
                host.setUserId(user.getId());
                host.setUsername(user.getFirstName(),user.getLastName());
                host.setEmail(user.getEmail());
                return host;
            }
        }
        throw new NoHostFoundException("No host found in this event!");
    }

}
