package tiketihub.authentication.dto;

import lombok.Data;
import tiketihub.authentication.response.AuthResponse;

@Data
public class TokenDTO {
    private AuthResponse authResponse;
    private String token;
    private String purpose;

    public String toString() {
        return "\nauthResponse= " + authResponse.getStatus() + "::" +
                authResponse.getMessage() + "\n" +
                "token='" + token + "\n" +
                "purpose='" + purpose + "\n";
    }

}
