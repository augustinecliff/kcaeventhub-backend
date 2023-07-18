package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.OrganizedEvent;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizedEventRepository extends CrudRepository<OrganizedEvent, UUID> {
    Optional<OrganizedEvent> findByUsername(String username);
}

// 'OrganizedEvent' domain type or valid projection interface expected here