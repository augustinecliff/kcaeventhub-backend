package tiketihub.api.event.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends CrudRepository<Event, UUID> {
    List<Event> findAllByActive(@NotNull boolean isActive);
}
