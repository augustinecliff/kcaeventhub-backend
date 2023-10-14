package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.api.event.model.Attendee;
import tiketihub.api.event.model.Event;
import tiketihub.user.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class EventParticipantDto {
    private UUID participantId;

    private String participantRole;

    private Map<String,Object> participantData;

    public Map<String,Object> setUser(User user) {
        return Map.of("userId", user.getId(),
                "email", user.getEmail(),
                "gender", user.getGender());
    }

    public Set<EventParticipantDto> getParticipantDtos(Event event) {
        Set<EventParticipantDto> participantDtos = new HashSet<>();

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
