package tiketihub.authentication.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import tiketihub.api.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public <T> ResponseEntity<ApiResponse<T>> unauthorizedHandler() {
        ApiResponse<T> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED,
                "The resource you are trying to access requires authentication",
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public <T> ResponseEntity<ApiResponse<T>> forbiddenHandler() {
        ApiResponse<T> response = new ApiResponse<>(
                HttpStatus.FORBIDDEN,
                "You do not have access to the specified resource",
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
