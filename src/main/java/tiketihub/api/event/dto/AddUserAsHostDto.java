package tiketihub.api.event.dto;

import lombok.Data;

@Data
public class AddUserAsHostDto {
    private String  userEmail;
    private String accessLevel;
}
