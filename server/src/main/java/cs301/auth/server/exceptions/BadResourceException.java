package cs301.auth.server.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BadResourceException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    
    public BadResourceException(String username) {
        super("Invalid credentials or you have not enrolled into the system - "+username);
    }
}
