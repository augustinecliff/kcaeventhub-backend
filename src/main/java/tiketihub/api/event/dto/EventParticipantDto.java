package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.EventParticipant;
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

        for (EventParticipant participant : event.getParticipants()) {
            EventParticipantDto participantDto = new EventParticipantDto();
            participantDto.setParticipantId(participant.getId());
            participantDto.setParticipantRole(participant.getRole());
            participantDto.setParticipantData(setUser(participant.getUser()));
            participantDtos.add(participantDto);
        }

         return participantDtos;
    }
}
