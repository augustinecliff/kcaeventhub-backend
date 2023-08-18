package tiketihub.authentication.security.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class JwtDTO {
    public JwtDTO(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }
    private final String userId;
    private final String email;
}
