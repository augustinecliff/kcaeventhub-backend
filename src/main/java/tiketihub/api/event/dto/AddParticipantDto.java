package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.user.User;

@Data
public class AddParticipantDto {
    private String role;
    private User user;
}
