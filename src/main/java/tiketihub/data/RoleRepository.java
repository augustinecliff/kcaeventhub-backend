package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
}
