package tiketihub.api.event.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.EventParticipant;

import java.util.UUID;

@Repository
public interface EventRepository extends CrudRepository<Event, UUID> {
    Event findByParticipant(EventParticipant participant);
}
