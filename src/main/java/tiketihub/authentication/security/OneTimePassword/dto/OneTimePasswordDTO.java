package tiketihub.authentication.security.OneTimePassword.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OneTimePasswordDTO {
    private UUID userId;
    private int code;

    public OneTimePasswordDTO(UUID userId, int code) {
        this.userId = userId;
        this.code = code;
    }
}
