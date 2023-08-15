package tiketihub.api.event.exceptions;

public class NonUniqueEventException extends RuntimeException{
    public NonUniqueEventException(String message) {
        super(message);
    }
}
