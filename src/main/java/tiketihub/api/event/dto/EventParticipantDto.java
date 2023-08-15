package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.user.User;

@Data
public class EventParticipantDto {
    private String role;
    private User user;
}
