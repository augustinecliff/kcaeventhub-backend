package tiketihub.authentication.exceptions;

public class BlackListedTokenException extends RuntimeException{
    public BlackListedTokenException(String message) {
        super(message);
    }
}
