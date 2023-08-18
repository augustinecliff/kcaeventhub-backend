package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.api.event.model.Category;

import java.time.LocalDate;

@Data
public class CreateEventDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long duration; // hours
    private Float pricing;
    private String venue;
    private Long capacity;
    private Long ageRestriction;
    private String description;
    private Category category;
}
