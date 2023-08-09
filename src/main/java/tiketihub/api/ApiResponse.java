package tiketihub.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private final String status;
    private final String message;
    private final T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.toString().substring(4)+": "+ status.value();
        this.message = message;
        this.data = data;
    }
}
