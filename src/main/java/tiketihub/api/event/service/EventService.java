package tiketihub.api.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            if(eventRepo.existsByTitle(eventDto.getTitle())) throw new RuntimeException("An event with a similar title Already Exists");
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


   /* public EventDetailsDto editEvent(UUID id , EditEventDto eventUpdate, String token) {
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
    }*/


    public String enrollUserAsHost(UUID eventId, String userEmail, String token) {
        log.info(("addNewUser() -> point: 1"));
        return eventRepo.findById(eventId)
                .map(event -> {
                    log.info(("point: 1"));
                    if (
                            jwtUtil.getUserIdAndEmailFromToken(token).getUserId()
                            .contentEquals(String.valueOf(eventRepo.findById(eventId).get().getOrganizer().getUser().getId()))) {
                        boolean expected = false;
                        String email =  jwtUtil.getUserIdAndEmailFromToken(token).getEmail();
                        if (isUserAnEventMember(event, userEmail)) {
                            throw new UserAlreadyExistsException(userEmail
                                    +" is already a member of '"+event.getTitle()+"'");
                        }
                        else if (isUserAnEventMember(event, userEmail) == expected) {
                        log.info(("point: 2"));
                        AddParticipantDto participantDto = new AddParticipantDto();
                        User user  = userRepo.findByEmail(userEmail)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "A user with the email "+userEmail+" does not exist!"));
                        participantDto.setRole("USER");
                        participantDto.setUser(user);
                        log.info(("point: 3"));
                        Attendee attendee = createOrSetAttendee(user, participantDto);
                        log.info(("point: 4"));
                        event.getAttendees().add(attendee);
                        log.info(("point: 5"));
                        eventRepo.save(event);
                        log.info(("point: 6"));
                        log.info("Participant added.");
                        return userEmail;
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
    /*private String eventUserAccessLevel(Event event,String token) {
        //log.info("eventUserAccessLevel");
        System.out.println(EventDetailsDto.hostUser(event).getUserId() +" = "+jwtUtil.getUserIdAndEmailFromToken(token).getUserId());
        System.out.println(""+EventDetailsDto.coHostUser(event).getUserId());
        System.out.println(""+EventDetailsDto.guestUser(event).getUserId().toString() +" = "+jwtUtil.getUserIdAndEmailFromToken(token).getUserId());
        if (String.valueOf(EventDetailsDto.hostUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            log.info("You are a: >Host");
            return "HOST";
        }
        else if (String.valueOf(EventDetailsDto.coHostUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            log.info("You are a: >Co-host");
            return "CO-HOST";
        }
        else if (String.valueOf(EventDetailsDto.guestUser(event).getUserId())
                .contentEquals(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())) {
            log.info("You are a: >Guest");
            return "GUEST";
        }
        log.info("You are a: >''");
        return "";
    }*/
    private boolean isUserAnEventMember(Event existingEvent, String email) {
        if (existingEvent.getAttendees().stream().anyMatch(event -> event.getUser().getEmail().equals(email))) {
            return true;
        }

        else return existingEvent.getOrganizer().getUser().getEmail().equals(email);
    }


    public List<Attendee> viewEventParticipants(UUID id, String token) {
        return eventRepo.findById(id)
                .map(event -> {
                    log.info(String.valueOf(String.valueOf(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()).
                            contentEquals(String.valueOf(event.getOrganizer().getUser().getId()))));
                    if (String.valueOf(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()).
                            contentEquals(String.valueOf(event.getOrganizer().getUser().getId()))) {
                        EventParticipantDto participants = new EventParticipantDto();
                        log.info("view Participants md");
                        event.getAttendees().stream().map(attendee -> attendee.getUser().getEmail()).forEach(System.out::println);

                        return event.getAttendees();
                    }
                    else throw new AccessDeniedException("Access denied :Only Host can see participant list");
                })
                .orElseThrow(() -> new EventNotFoundException("Event id not found!"));
    }

    public String deleteParticipant(UUID eventId, UUID participantId, String token) {
        return eventRepo.findById(eventId)
                .map(event -> {
                    if(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()
                            .contentEquals(String.valueOf(eventRepo.findById(eventId).get().getOrganizer().getUser().getId()))) {
                        return deleteProcessor(participantId, event);
                    }
                    else throw new AccessDeniedException("Access denied :Only Host can delete participants");
                })
                .orElseThrow(() -> new ParticipantNotFoundException("No event with the specified id found!"));
    }

    public EventDetailsDto archiveEvent(UUID eventId, String token) {
        eventRepo.findById(eventId)
                .map(event -> {
                    if(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()
                            .contentEquals(String.valueOf(eventRepo.findById(eventId).get().getOrganizer().getUser().getId()))) {
                        if (event.isActive()) {
                            event.setActive(false);
                        }
                        else {
                            throw new EventNotFoundException("Event has already been deleted!");
                        }
                        eventRepo.save(event);
                        return new EventDetailsDto(event);
                    }
                    else {
                        throw new AccessDeniedException("Access denied :Only Host can archive event");
                    }
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

    public String unEnrollUser(UUID eventId, String token) {
        log.info("unEnrollUser: st");
        User userFromToken = userRepo.findById(UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(token).getUserId())).orElseThrow(() -> new UsernameNotFoundException("User with id: "+jwtUtil.getUserIdAndEmailFromToken(token).getUserId()+" does not exist."));
        return eventRepo.findById(eventId)
                .map(event -> {
                    log.info("unEnrollUser: md1");
                    log.info("As Organiser: "+String.valueOf(String.valueOf(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()).
                            contentEquals(String.valueOf(event.getOrganizer().getUser().getId()))));
                    log.info("As Attendee: "+String.valueOf(String.valueOf(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()).
                            contentEquals(String.valueOf(event.getAttendees().stream()
                                    .map(attendee -> attendee.getUser().getId())
                                    .filter(userId -> String.valueOf(userId).contentEquals(String.valueOf(userFromToken.getId()))).findFirst().orElseThrow(() -> new AccessDeniedException("Participant does not exist1"))))));
                    //Attendee currentAttendee = attendeeRepo.findByUser(userFromToken).orElseThrow(() -> new ParticipantNotFoundException("No user with the specified id found!"));
                    if(
                            String.valueOf(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()).
                                    contentEquals(String.valueOf(event.getOrganizer().getUser().getId()))
                                    ||
                            String.valueOf(jwtUtil.getUserIdAndEmailFromToken(token).getUserId()).
                                            contentEquals(String.valueOf(event.getAttendees().stream()
                                                    .map(attendee -> attendee.getUser().getId())
                                                    .filter(userId -> String.valueOf(userId).contentEquals(String.valueOf(userFromToken.getId()))).findFirst().orElseThrow(() -> new AccessDeniedException("Participant does not exist2"))))
                    ) {
                        log.info("unEnrollUser: md2");
                       // UUID userId = UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(token).getUserId());
                        eventRepo.findById(eventId).get().getAttendees().stream()
                                                .map(attendee -> attendee)
                                                .filter(attendee -> String.valueOf(attendee.getUser().getId()).contentEquals(String.valueOf(userFromToken.getId()))).findFirst()
                                .ifPresent(attendee -> {
                                    log.info("unEnrollUser: nd");
                                    deleteProcessor(attendee.getId(), event);
                                });
                        /*for (Attendee attendee : eventRepo.findById(eventId).get().getAttendees() ) {
                            if (event.getOrganizer().getUser().getId() != userId) {
                                if (String.valueOf(attendee.getUser().getId()).equals(String.valueOf(userId))) {
                                    log.info("unEnrollUser: nd");
                                    return deleteProcessor(attendee.getId(), event);
                                }
                                else throw  new AccessDeniedException("Participant does not exist3");
                            }
                            else  throw new AccessDeniedException("Host cannot quit event");

                        }*/

                    }
                    else throw new AccessDeniedException("You are not in the list of attendees");
                    return new String();
                })
              .orElseThrow(() -> new ParticipantNotFoundException("No user with the specified id found!"));
    }

    private String deleteProcessor(UUID attendeeId, Event event) {
        if (attendeeRepo.existsById(attendeeId)) {
            final Attendee attendeeToRemove = event.getAttendees().stream()
                    .filter(attendee -> String.valueOf(attendee.getId()).contentEquals(String.valueOf(attendeeId))).findFirst().orElseThrow(() -> new  ParticipantNotFoundException("Attendee with id: "+attendeeId+" does not exist"));

            if (attendeeToRemove != null) {
                eventRepo.findById(event.getId()).ifPresent(thisevent -> {
                    thisevent.getAttendees().remove(attendeeToRemove);
                    eventRepo.save(thisevent);
                    log.info("Attendee "+attendeeId+" has been removed successfully from the event");
                });
                return String.valueOf(attendeeId);
            } else {
                log.info("No attendee found with the attendeeId: "+attendeeId+" in the event");
                throw new UnsupportedOperationException("No attendee found with the attendeeId: "+attendeeId+" in the event");
            }
        } else {
            throw new ParticipantNotFoundException("No participant with attendeeId: "+attendeeId+" in exists");
        }
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

    public Set<EventDetailsDto> getEventsOrganizedBy(String authToken) {
        log.info("Is this processed (getEventsOrganizedBy) ?");
        User user = userRepo.findById(UUID.fromString(jwtUtil.getUserIdAndEmailFromToken(authToken).getUserId())).orElseThrow(() -> new RuntimeException("User with userId :" + jwtUtil.getUserIdAndEmailFromToken(authToken).getUserId()  + " does not exist"));
        return eventRepo.findEventsByActiveAndOrganizer(true,
                organizerRepo.findByUser(user).
                        orElseThrow(() -> new OrganizerNotFoundException("The organizer with userId "+ user.getId() +" does not exist")))
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
                                    .collect(Collectors.toList()))
            );
            /*organizerRepo.findByUser(user).ifPresent(
                    organizer -> upcomingEvents.addAll(
                            organizer.getEvents()
                                    .stream().map(EventDetailsDto::new)
                                    .collect(Collectors.toSet()))
            );*/
            return upcomingEvents;
        }
        else throw new InvalidTokenException("Invalid token");
    }
}
