package tiketihub.authentication.dto;

import lombok.Data;

@Data
public class PasswardDTO {
    private String password;
    private String confirmPassword;
}
