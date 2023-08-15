package tiketihub.api.event.dto;

import lombok.Data;
import tiketihub.api.event.model.Event;

import java.time.LocalDate;

@Data
public class EditEventDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long duration;
    private Float pricing;
    private String venue;
    private Long capacity;
    private Long ageRestriction;
    private String description;
    private CategoryDto category;

}
