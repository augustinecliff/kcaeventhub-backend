package tiketihub.authentication.exceptions;

public class InvalidUserCredentialException extends RuntimeException {
    public InvalidUserCredentialException(String message) {
        super(message);
    }
}
