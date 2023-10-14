package tiketihub.api.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tiketihub.api.event.dto.*;
import tiketihub.api.event.exceptions.*;
import tiketihub.api.event.model.Attendee;
import tiketihub.api.event.model.Category;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.Organizer;
import tiketihub.api.event.repository.AttendeeRepository;
import tiketihub.api.event.repository.CategoryRepository;
import tiketihub.api.event.repository.EventRepository;
import tiketihub.api.event.repository.OrganizerRepository;
import tiketihub.authentication.exceptions.InvalidTokenException;
import tiketihub.authentication.exceptions.UserAlreadyExistsException;
import tiketihub.authentication.security.jwt.JWTUtil;
import tiketihub.emailconfig.EmailConfig;
import tiketihub.emailconfig.templates.EmailTemplates;
import tiketihub.user.User;
import tiketihub.user.UserRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventService {

    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private OrganizerRepository organizerRepo;
    @Autowired
    private AttendeeRepository attendeeRepo;
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
            log.info(("point: 4.0"));
            AddParticipantDto participant = new AddParticipantDto();
            User user = userRepo.findById(UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())).
                    orElseThrow(() -> new UsernameNotFoundException("The user does not Exist!"));
            participant.setUser(user);
            log.info(("point: 5.0"));
            Organizer organizer = createOrSetOrganizer(user, participant);
            log.info(("point: 5.1"));
            newEvent.setOrganizer(organizer);
            log.info(("point: 6"));
            newEvent.setActive(true);
            log.info(("point: 7"));
            UUID id = eventRepo.save(newEvent).getId();
            log.info("event id: "+id);
            log.info(("point: 8"));
            EmailTemplates template = new EmailTemplates();
            log.info(("point: 9"));
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
                        User user  = userRepo.findByEmail(addUserDto.getUserEmail())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "A user with the email "+addUserDto.getUserEmail()+" does not exist!"));
                        participantDto.setRole(addUserDto.getAccessLevel());
                        participantDto.setUser(user);
                        log.info(("point: 5"));
                        Attendee attendee = createOrSetAttendee(user, participantDto);
                        log.info(("point: 6"));
                        event.getAttendees().add(attendee);
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
                        User user = userRepo.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "The user with email "+email+" does not exist!"));
                        participantDto.setRole("GUEST");
                        participantDto.setUser(user);
                        log.info(("point: 4"));
                        Attendee attendee = createOrSetAttendee(user, participantDto);
                        event.getAttendees().add(attendeeRepo.save(attendee));
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
        else if (String.valueOf(EventDetailsDto.guestUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            return "GUEST";
        }
        return "";
    }
    private boolean isUserAnEventMember(Event existingEvent, String email) {
        if (existingEvent.getAttendees().stream().anyMatch(event -> event.getUser().getEmail().equals(email))) {
            return true;
        }

        else return existingEvent.getOrganizer().getUser().getEmail().equals(email);
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

    private String deleteProcessor(UUID attendeeId, Event event) {
        if(attendeeRepo.existsById(attendeeId)) {
            for (Attendee attendee : event.getAttendees()) {
                if (String.valueOf(attendee.getId()).contentEquals(attendeeId.toString())) {
                    event.getAttendees().remove(attendee);
                    attendeeRepo.delete(attendee);
                    break;
                }
            }
            return String.valueOf(attendeeId);
        }
        else throw new ParticipantNotFoundException("No participant with the specified id exists");
    }

    private Organizer createOrSetOrganizer(User user, AddParticipantDto participant) {
        if (organizerRepo.findByUser(user).isPresent()) {
            return organizerRepo.findByUser(user).get();
        }
        else {
            return organizerRepo.save(Organizer.addOrganizer(participant));
        }
    }
    private Attendee createOrSetAttendee(User user, AddParticipantDto participant) {
        if (attendeeRepo.findByUser(user).isPresent()) {
            return attendeeRepo.findByUser(user).get();
        }
        else {
            return attendeeRepo.save(Attendee.addAttendee(participant));
        }
    }

    public Set<EventDetailsDto> getEventsOrganizedBy(UUID organizerId) {
        log.info("Is this processed (getEventsOrganizedBy) ?");
        return eventRepo.findEventsByActiveAndOrganizer(true,
                organizerRepo.findById(organizerId).
                        orElseThrow(() -> new OrganizerNotFoundException("The organizer "+ organizerId +" does not exist")))
                .stream().map(EventDetailsDto::new).collect(Collectors.toSet());
    }

    public Set<EventDetailsDto> getUpcomingEventsForUser(String token) {
        if(jwtUtil.validateToken(token)) {
            UUID userId = UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(token).getUserId());

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("The user "+userId+" does not exist!"));

            Set<EventDetailsDto> upcomingEvents = new HashSet<>();
            attendeeRepo.findByUser(user).ifPresent(
                    attendee -> upcomingEvents.addAll(
                            attendee.getEvents()
                                    .stream().map(EventDetailsDto::new)
                                    .collect(Collectors.toSet()))
            );
            organizerRepo.findByUser(user).ifPresent(
                    organizer -> upcomingEvents.addAll(
                            organizer.getEvents()
                                    .stream().map(EventDetailsDto::new)
                                    .collect(Collectors.toSet()))
            );
            return upcomingEvents;
        }
        else throw new InvalidTokenException("Invalid token");
    }
}
