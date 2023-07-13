package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.EventCategory;

import java.util.UUID;

@Repository
public interface EventCategoryRepository extends CrudRepository<EventCategory, UUID> {
}
