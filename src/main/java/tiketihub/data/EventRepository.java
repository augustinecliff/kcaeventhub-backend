package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.Event;

import java.util.UUID;

@Repository
public interface EventRepository extends CrudRepository<Event, UUID> {
}