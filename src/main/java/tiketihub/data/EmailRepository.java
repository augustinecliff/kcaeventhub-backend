package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import tiketihub.entity.Email;

import java.util.UUID;

public interface EmailRepository extends CrudRepository<Email, UUID> {
}
