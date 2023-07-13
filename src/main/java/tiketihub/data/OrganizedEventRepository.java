package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.OrganizedEvent;
import tiketihub.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizedEventRepository extends CrudRepository<OrganizedEvent, UUID> {
    Iterable<OrganizedEvent> findAllByEventsIn(List<User> users);
}
