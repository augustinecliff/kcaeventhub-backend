package tiketihub.api.event.dto;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Data
public class EventUserAccessLevelDto {
    private UUID userId;
    private String username;
    private String email;

    public void setUsername(String firstName,String lastName) {
        char first = firstName.toUpperCase().trim().charAt(0);
        firstName = first + firstName.trim().substring(1).toLowerCase();
        first = lastName.toUpperCase().trim().charAt(0);
        lastName = first + lastName.trim().substring(1).toLowerCase();
        this.username = firstName + " " + lastName;
        Logger logger = LoggerFactory.getLogger(EventUserAccessLevelDto.class);
        logger.info("\n::>"+this.username);
    }
}
