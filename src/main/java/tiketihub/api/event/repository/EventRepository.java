package tiketihub.api.event.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tiketihub.api.event.model.Event;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
   // List<Event> findAllByActive(@NotNull boolean isActive);

    Page<Event> findByActive(@NotNull boolean isActive,Pageable pageable);
}
