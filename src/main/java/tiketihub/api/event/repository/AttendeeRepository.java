package tiketihub.api.event.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.Attendee;
import tiketihub.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendeeRepository extends CrudRepository<Attendee, UUID> {
    Optional<Attendee> findByUser(User user);
}
