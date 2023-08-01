package tiketihub.authentication.dto;

import lombok.Data;
import tiketihub.authentication.response.AuthResponse;

@Data
public class TokenDTO {
    private AuthResponse authResponse;
    private String token;
    private boolean status;

}
