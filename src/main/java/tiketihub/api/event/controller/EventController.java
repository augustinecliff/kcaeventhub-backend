package tiketihub.api.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tiketihub.api.ApiResponse;
import tiketihub.api.event.dto.AddUserAsHostDto;
import tiketihub.api.event.dto.CreateEventDto;
import tiketihub.api.event.dto.EditEventDto;
import tiketihub.api.event.dto.EventDetailsDto;
import tiketihub.api.event.model.Category;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.EventParticipant;
import tiketihub.api.event.service.EventService;
import tiketihub.authentication.dto.EmailDTO;


import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/event")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/categories")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Iterable<Category>>> eventCategories() {
        try {

                        return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "EventCategories",
                            service.getEventCategories()
                    )
            );
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<EventDetailsDto>> createEvent(@RequestBody CreateEventDto eventDto,
                                                          @RequestHeader("Authorization") String authToken) {
        try {
            Event event = service.createNewEvent(eventDto,authToken);
            EventDetailsDto detailsToCreatedEvent = service.viewEventDetails(event.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(
                    HttpStatus.CREATED,
                    "The event has been created successfully!",
                    detailsToCreatedEvent));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }
    @GetMapping("/details")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<EventDetailsDto>> eventDetails(@RequestParam("eventId")UUID id) {
        try {
            EventDetailsDto eventDetails = service.viewEventDetails(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Event details fetched successfully!",
                            eventDetails
                            ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PatchMapping("/edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<EventDetailsDto>> editEvent(@RequestParam("eventId")UUID id,
                                                                  @RequestBody EditEventDto eventUpdate,
                                                                  @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(id));
            EventDetailsDto editEvent = service.editEvent(id, eventUpdate, authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Details to event '" +
                                    editEvent.getTitle()+"' by host ' have " +
                                    "been updated successfully!",
                            editEvent
            ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PatchMapping("/host/adduser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<AddUserAsHostDto>> addUserAsHost(@RequestParam("eventId")UUID id,
                                                                      @RequestBody AddUserAsHostDto addUserDto,
                                                                      @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(id));
            AddUserAsHostDto addUser = service.enrollUserAsHost(id,addUserDto,authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                                    "New user with email: "+addUser.getUserEmail()+"been added successfully!",
                            addUser
                    ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }
    @GetMapping("/host/viewparticipants/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Set<EventParticipant>>> participantsToEvent(@PathVariable("eventId")UUID id,
                                                                                  @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(id));
            Set<EventParticipant> participants = service.viewEventParticipants(id, authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Participants fetched successfully!",
                            participants
                    ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @DeleteMapping("/host/remove-participant")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Set<EventParticipant>>> removeParticipantAsHost(@RequestParam("eventId")UUID eventId,
                                                                                  @RequestParam("participantId") UUID participantId,
                                                                                  @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");

            String participant = service.deleteParticipant(eventId, participantId, authToken);
                                    return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Participant "+participant+"has been removed successfully successfully!",
                            null
                    ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PatchMapping("/host/archive-event/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<EventDetailsDto>> archiveEventAsHost(@PathVariable("eventId") UUID eventId,
                                                                                      @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");


            EventDetailsDto details = service.archiveEvent(eventId, authToken);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Event '"+details.getTitle()+"' has been archived successfully!",
                            details
                    ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }
    @GetMapping("/events")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<EventDetailsDto>>> browseEvents(@RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");

            List<EventDetailsDto> details = service.browseAllEvents();

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "All events fetched successfully!",
                            details
                    ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }

    }
    @PatchMapping("/guest/enroll/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> enrollToEventAsUser(@PathVariable("eventId")UUID id,
                                                                       @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(id));
            String addedUserEmail = service.selfEnrollUser(id,authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "New user with email: "+addedUserEmail+"been added successfully!",
                            addedUserEmail
                    ));
        }
        catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(
                            HttpStatus.CONFLICT,
                            e.getMessage(),
                            null
                    ));
        }
    }

}
