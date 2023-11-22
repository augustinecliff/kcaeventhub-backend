package tiketihub.authentication.dto;

import lombok.Data;
import lombok.Getter;

@Getter
public class LoginDTO {
    private String email;
    private String password;

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
