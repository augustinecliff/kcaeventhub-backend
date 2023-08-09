package tiketihub.authentication.response;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class AuthResponse<T> {
    private String status;
    private String message;
    private T data;

    public AuthResponse(HttpStatus status, String message, T data) {
        this.status = status.toString().substring(4)+": "+ status.value();
        this.message = message;
        this.data = data;
    }
    public AuthResponse(HttpServletResponse status, String message) {
        this.status = status.toString().substring(4)+": "+ status;
        this.message = message;
        this.data = data;
    }


}
