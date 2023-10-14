package tiketihub.authentication.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtDTO {
    public JwtDTO(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }
    private String userId;
    private final String email;
}
