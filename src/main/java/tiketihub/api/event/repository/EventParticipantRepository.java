package tiketihub.api.event.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.EventParticipant;
import tiketihub.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventParticipantRepository extends CrudRepository<EventParticipant, UUID> {
    Optional<EventParticipant> findByUser(User user);
}
