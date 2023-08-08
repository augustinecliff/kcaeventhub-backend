package tiketihub.authentication.response;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthResponse {
    private String status;
    private String message;
    public AuthResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
