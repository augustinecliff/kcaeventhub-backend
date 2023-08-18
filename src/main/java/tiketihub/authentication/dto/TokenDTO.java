package tiketihub.authentication.dto;

import lombok.Data;
import tiketihub.authentication.response.AuthResponse;

@Data
public class TokenDTO {
    private String token;
    private String purpose;


}
