package tiketihub.api.event.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BrowseEventsDto {
    private Map<String, Object> pageData;
    private List<EventDetailsDto> events;

}
