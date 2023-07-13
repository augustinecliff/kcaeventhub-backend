package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.Token;

import java.util.UUID;

@Repository
public interface TokenRepository extends CrudRepository<Token, UUID> {
}
