package tiketihub.api.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tiketihub.api.ApiResponse;
import tiketihub.api.event.dto.*;
import tiketihub.api.event.model.Attendee;
import tiketihub.api.event.model.Category;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.repository.AttendeeRepository;
import tiketihub.api.event.repository.EventRepository;
import tiketihub.api.event.service.EventService;
import tiketihub.user.User;
import tiketihub.user.UserRepository;


import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/event")
@CrossOrigin( origins = "*")
public class EventController {
    private final EventService service;
    @Autowired
    private AttendeeRepository attendeeRepo;
    @Autowired
    private EventRepository eventRepo;

    @Autowired
    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/alldetails")
    public ResponseEntity<Object> events() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(eventRepo.findById(UUID.fromString("02de4454-f057-42cc-b820-67bdb43d3c6b")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/categories")
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

    /*@PatchMapping("/edit")
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
    }*/

    @GetMapping("/host/viewparticipants/{eventId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Attendee>>> participantsToEvent(@PathVariable("eventId")UUID id,
                                                                                  @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(id));
            List<Attendee> participants = service.viewEventParticipants(id, authToken);
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

    @PatchMapping("/host/adduser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> addUserAsHost(@RequestParam("eventId")UUID eventId,
                                                                      @RequestBody EmailDto userEmail,
                                                                      @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(eventId));
            String email = service.enrollUserAsHost(eventId,userEmail.getUserEmail(),authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                                    "You have successfully Added: "+email,
                            email
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
    public ResponseEntity<ApiResponse<Set<Attendee>>> removeParticipantAsHost(@RequestParam("eventId")UUID eventId,
                                                                              @RequestParam("participantId") UUID participantId,
                                                                              @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");

            String attendee = service.deleteParticipant(eventId, participantId, authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Participant "+ attendee +" has been removed successfully successfully!",
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
    @GetMapping("/browse")
    public ResponseEntity<ApiResponse<BrowseEventsDto>> browseEvents(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                           @RequestParam(name = "size", defaultValue = "3") int size) {
        try {
            BrowseEventsDto eventDetails = service.browseAllEvents(page,size);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "All events fetched successfully!",
                            eventDetails
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
    @PatchMapping("/user/enroll/{eventId}")
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
                            "New attendee with email: "+addedUserEmail+" has enrolled successfully!",
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
    @PatchMapping("/user/un-enroll")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> unEnrollFromEventAsUser(@RequestParam("eventId")UUID eventId,
                                                                     @RequestHeader("Authorization")String authToken) {
        try {
            log.info("/user/un-enroll");
            authToken = authToken.replace("Bearer ", "");
            log.info(String.valueOf(eventId));
            String userEmail = service.unEnrollUser(eventId, authToken);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "New user with email: "+userEmail+" has un-enrolled successfully!",
                            userEmail
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
    @GetMapping("/upcoming-events")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Set<EventDetailsDto>>> viewUpcomingEventForUser( @RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Upcoming events for current user fetched successfully!",
                            service.getUpcomingEventsForUser(authToken)
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
    @GetMapping("/byorganizer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Set<EventDetailsDto>>> viewOrganizerSpecificEvents(@RequestHeader("Authorization")String authToken) {
        try {
            authToken = authToken.replace("Bearer ", "");
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            HttpStatus.OK,
                            "Events organized by current user fetched successfully!",
                            service.getEventsOrganizedBy(authToken)
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
