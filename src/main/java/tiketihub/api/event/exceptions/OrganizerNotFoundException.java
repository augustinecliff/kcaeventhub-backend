package tiketihub.api.event.exceptions;

public class OrganizerNotFoundException extends RuntimeException{
    public OrganizerNotFoundException(String message) {
        super(message);
    }
}
