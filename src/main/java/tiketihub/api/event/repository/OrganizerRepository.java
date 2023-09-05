package tiketihub.api.event.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.Organizer;
import tiketihub.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends CrudRepository<Organizer, UUID> {
    Optional<Organizer> findByUser(User user);
}
