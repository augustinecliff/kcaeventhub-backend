package tiketihub.api.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tiketihub.api.event.dto.*;
import tiketihub.api.event.exceptions.AccessDeniedException;
import tiketihub.api.event.exceptions.CategoryNotFoundExeption;
import tiketihub.api.event.exceptions.EventNotFoundException;
import tiketihub.api.event.exceptions.ParticipantNotFoundException;
import tiketihub.api.event.model.Category;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.EventParticipant;
import tiketihub.api.event.repository.CategoryRepository;
import tiketihub.api.event.repository.EventParticipantRepository;
import tiketihub.api.event.repository.EventRepository;
import tiketihub.authentication.exceptions.InvalidTokenException;
import tiketihub.authentication.exceptions.UserAlreadyExistsException;
import tiketihub.authentication.security.jwt.JWTUtil;
import tiketihub.emailconfig.EmailConfig;
import tiketihub.emailconfig.templates.EmailTemplates;
import tiketihub.user.UserRepository;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class EventService {

    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private EventParticipantRepository participantRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private EmailConfig sendEmail;
    public Iterable<Category> getEventCategories() {
        return categoryRepo.findAll();
    }

    public Event createNewEvent(CreateEventDto eventDto, String authToken) {
        log.info(("point: 1"));
        try {
                log.info(("point: 2"));
                String token = authToken.replace("Bearer ", "");

                Event newEvent = Event.buildEvent(eventDto);
                log.info(("point: 3"));
                newEvent.setCategory(
                        categoryRepo.findById(eventDto.getCategory().getId()).
                                orElseThrow(() ->
                                        new CategoryNotFoundExeption("The category posted does not exist!")));
                log.info(("point: 4"));

                AddParticipantDto participant = new AddParticipantDto();
                participant.setUser(
                        userRepo.findById(UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())).
                                orElseThrow(() -> new UsernameNotFoundException("The user does not Exist!"))
                );
                log.info(("point: 5"));

                participant.setRole("HOST");
                EventParticipant hostParticipant =  participantRepo.save(EventParticipant.addParticipant(participant));
                log.info(("point: 6"));
                newEvent.setParticipants(Set.of(
                        hostParticipant
                ));
                log.info(("point: 7"));
                newEvent.setActive(true);
                log.info(("point: 8"));
                UUID id = eventRepo.save(newEvent).getId();
                log.info("event id: "+id);
                 log.info(("point: 9"));
                EmailTemplates template = new EmailTemplates();
                log.info(("point: 10"));
                sendEmail.sendMailWithAttachment(
                        jwtUtil.getUserIdAndEmailFromToken(token).getEmail(),
                        "New event creation",
                        template.getNewEvent()
                );
                log.info(("point: 10"));

                return newEvent;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public EventDetailsDto viewEventDetails(UUID id) {
        log.info(String.valueOf(eventRepo.findById(id)));
        return eventRepo.findById(id)
                .map(EventDetailsDto::new)
                .orElseThrow(() -> new EventNotFoundException("Event id not found!"));
    }


    public EventDetailsDto editEvent(UUID id , EditEventDto eventUpdate, String token) {
        if(jwtUtil.validateToken(token)) {
            return eventRepo.findById(id)
                    .map(event ->
                    {
                        if (eventUserAccessLevel(event, token).contentEquals("HOST") ||
                                eventUserAccessLevel(event, token).contentEquals("CO-HOST")) {
                            event.editEvent(eventUpdate);
                            event = eventRepo.save(event);
                            return viewEventDetails(event.getId());
                        }
                         else throw new AccessDeniedException("Access to denied :Requires host/co-host to access");

                    })
                    .orElseThrow(() -> new EventNotFoundException("Event id not found!"));
        }
        else throw new InvalidTokenException("The token you entered is invalid!");
    }


    public AddUserAsHostDto enrollUserAsHost(UUID id, AddUserAsHostDto addUserDto, String token) {
        log.info(("addNewUser() -> point: 1"));
        return eventRepo.findById(id)
                .map(event -> {
                    log.info(("point: 2"));
                    if (addUserDto.getAccessLevel().contentEquals("HOST")) throw new UserAlreadyExistsException("Event cannot have 2 Hosts");
                    log.info(("point: 3"));
                    if (eventUserAccessLevel(event, token).contentEquals("HOST")) {
                        boolean expected = false;
                        String email =  jwtUtil.getUserIdAndEmailFromToken(token).getEmail();
                        if (isUserAnEventMember(event, addUserDto.getUserEmail()) == expected) {

                        if (isUserAnEventMember(event, addUserDto.getUserEmail())) {
                            throw new UserAlreadyExistsException(addUserDto.getUserEmail()
                                    +" is already a member of '"+event.getTitle()+"'");
                        }
                        log.info(("point: 4"));
                        AddParticipantDto participantDto = new AddParticipantDto();
                        participantDto.setRole(addUserDto.getAccessLevel());
                        participantDto.setUser(userRepo.findByEmail(addUserDto.getUserEmail())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "The user with email "+addUserDto.getUserEmail()+" does not exist!")));
                        log.info(("point: 5"));
                        EventParticipant participant = participantRepo.save(EventParticipant.addParticipant(participantDto));
                        log.info(("point: 6"));
                        event.getParticipants().add(participant);
                        log.info(("point: 7"));
                        eventRepo.save(event);
                        log.info(("point: 8"));
                        log.info("Participant added.");
                        return addUserDto;
                        }
                        else throw new UserAlreadyExistsException(email+" is already a member of '"+event.getTitle()+"'");
                    }
                    else throw new AccessDeniedException("Access to denied :Only Host can manually add host/guest");
                }).orElseThrow(() -> new EventNotFoundException("Event id not found!"));
    }
    public String selfEnrollUser(UUID id, String token) {
        log.info(("addNewUser() -> point: 1"));
        return eventRepo.findById(id)
                .map(event -> {
                    log.info(("point: 2"));
                    String email = jwtUtil.getUserIdAndEmailFromToken(token).getEmail();
                    boolean expected = false;
                    if (isUserAnEventMember(event, email) == expected) {
                        log.info(("point: 3"));
                        AddParticipantDto participantDto = new AddParticipantDto();
                        participantDto.setRole("GUEST");

                        participantDto.setUser(userRepo.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "The user with email "+email+" does not exist!")));
                        log.info(("point: 4"));
                        event.getParticipants().add(participantRepo.save(EventParticipant.addParticipant(participantDto)));
                        log.info(("point: 5"));
                        eventRepo.save(event);
                        log.info(("point: 6"));
                        log.info("Participant added.");
                        return email;
                    }
                    else throw new UserAlreadyExistsException(email+" is already a member of '"+event.getTitle()+"'");
                }).orElseThrow(() -> new EventNotFoundException("Event id not found!"));
    }
    private String eventUserAccessLevel(Event event,String token) {
        if (String.valueOf(EventDetailsDto.hostUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            return "HOST";
        }
        else if (String.valueOf(EventDetailsDto.coHostUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            return "CO-HOST";
        }
        else if (String.valueOf(EventDetailsDto.hostUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            return "GUEST";
        }
        return "";
    }
    private boolean isUserAnEventMember(Event existingEvent, String email) {

        return existingEvent.getParticipants().stream()
                .anyMatch(event -> event.getUser().getEmail().contentEquals(email));

    }

    public Set<EventParticipantDto> viewEventParticipants(UUID id, String token) {
        return eventRepo.findById(id)
                .map(event -> {
                    if (eventUserAccessLevel(event, token).contentEquals("HOST") ||
                            eventUserAccessLevel(event, token).contentEquals("CO-HOST")) {
                        EventParticipantDto participants = new EventParticipantDto();

                        return participants.getParticipantDtos(event);
                    }
                    else throw new AccessDeniedException("Access to denied :Only Host/Co-Host can see participant list");
                })
                .orElseThrow(() -> new EventNotFoundException("Event id not found!"));
    }

    public String deleteParticipant(UUID eventId, UUID participantId, String token) {
        return eventRepo.findById(eventId)
                .map(event -> {
                    if(eventUserAccessLevel(event, token).contentEquals("HOST")) {
                        return deleteProcessor(participantId, event);
                    }
                    else throw new AccessDeniedException("Access to denied :Only Host can delete participants");
                })
                .orElseThrow(() -> new ParticipantNotFoundException("No user with the specified id found!"));
    }

    public EventDetailsDto archiveEvent(UUID eventId, String token) {
        eventRepo.findById(eventId)
                .map(event -> {
                    if(eventUserAccessLevel(event, token)
                            .contentEquals("HOST")) {
                        event.setActive(false);
                        eventRepo.save(event);
                        return new EventDetailsDto(event);
                    }
                    else throw new AccessDeniedException("Access denied :Only Host can archive event");
                });
        return viewEventDetails(eventId);
    }

    public BrowseEventsDto browseAllEvents(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepo.findByActive(true,paging);

        BrowseEventsDto events = new BrowseEventsDto();

        events.setEvents(eventPage.stream()
                .map(EventDetailsDto::new)
                .toList());
        events.setPageData(
                Map.of("currentPage", eventPage.getNumber(),
                        "totalEvents",eventPage.getTotalElements(),
                        "totalPages",eventPage.getTotalPages())
        );
      return events;
    }

    public String unEnrollUser(UUID eventId, UUID participantId, String token) {
        return eventRepo.findById(eventId)
                .map(event -> {
                    if(eventUserAccessLevel(event, token).contentEquals("CO-HOST") ||
                            eventUserAccessLevel(event, token).contentEquals("GUEST")) {
                       return deleteProcessor(participantId, event);
                    }
                    else throw new AccessDeniedException("Host cannot quit event");
                })
                .orElseThrow(() -> new ParticipantNotFoundException("No user with the specified id found!"));
    }

    private String deleteProcessor(UUID participantId, Event event) {
        if(participantRepo.existsById(participantId)) {
            for (EventParticipant participant : event.getParticipants()) {
                if (String.valueOf(participant.getId()).contentEquals(participantId.toString())) {
                    event.getParticipants().remove(participant);
                    participantRepo.delete(participant);
                    break;
                }
            }
            return String.valueOf(participantId);
        }
        else throw new ParticipantNotFoundException("No participant with the specified id exists");
    }
}
