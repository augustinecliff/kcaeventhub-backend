package tiketihub.api.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tiketihub.api.ApiResponse;
import tiketihub.user.UserDTO;

@RestController
@Slf4j
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserDTO>> viewProfile(@RequestHeader("Authorization") String authToken) {
        try {
            ApiResponse<UserDTO> response = new ApiResponse<>(
                    HttpStatus.OK,
                    "Authorization successful",
                    profileService.validateTokenAndFetchProfile(authToken)
            );
            return ResponseEntity.status(HttpStatus.OK)
                   .body(response);
        }
        catch (Exception exc) {
            log.info(exc.getMessage());
            ApiResponse<UserDTO> response = new ApiResponse<>(
                    HttpStatus.OK,
                    exc.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @PatchMapping("/edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserDTO>> editProfile(@RequestHeader("Authorization") String authToken,
                                                            @RequestBody UserDTO userDTO) {
        try {
            ApiResponse<UserDTO> response = new ApiResponse<>(
                    HttpStatus.OK,
                    "Profile updated successful",
                    profileService.validateTokenAndEditProfile(authToken,userDTO)
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
        }
        catch (Exception exc) {
            log.info(exc.getMessage());
            ApiResponse<UserDTO> response = new ApiResponse<>(
                    HttpStatus.CONFLICT,
                    exc.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}


