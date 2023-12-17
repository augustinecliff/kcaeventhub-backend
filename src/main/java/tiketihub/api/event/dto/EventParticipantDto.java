package tiketihub.api.event.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import tiketihub.api.event.model.Attendee;
import tiketihub.api.event.model.Event;
import tiketihub.user.User;

import java.util.*;

@Data
@Slf4j
public class EventParticipantDto {
    private UUID participantId;

    private String participantRole;

    private Map<String,Object> participantData;

    public Map<String,Object> setUser(User user) {
        return Map.of(
                "userId", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail());
    }

    public List<EventParticipantDto> getParticipantDtos(Event event) {
        List<EventParticipantDto> participantDtos = new LinkedList<>();

        for (Attendee attendee : event.getAttendees()) {
            EventParticipantDto participantDto = new EventParticipantDto();
            participantDto.setParticipantId(attendee.getId());
            participantDto.setParticipantRole(attendee.getRole());
            participantDto.setParticipantData(setUser(attendee.getUser()));

            participantDtos.add(participantDto);
        }
         return participantDtos;
    }
}
