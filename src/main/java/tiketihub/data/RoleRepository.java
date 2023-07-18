package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);

}
