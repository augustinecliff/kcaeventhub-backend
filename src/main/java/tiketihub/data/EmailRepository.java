package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import tiketihub.entity.Email;
import tiketihub.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface EmailRepository extends CrudRepository<Email, UUID> {
    Optional<User> findByUser(User name);
}
