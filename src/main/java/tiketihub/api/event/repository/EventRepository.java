package tiketihub.api.event.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.Event;
import tiketihub.api.event.model.Organizer;

import java.util.Set;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Page<Event> findByActive(@NotNull boolean isActive,Pageable pageable);
    Set<Event> findByActive(@NotNull boolean isActive);
    Set<Event> findEventsByActiveAndOrganizer(@NotNull boolean isActive, Organizer organizer);
}
