package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.PaymentOption;

import java.util.UUID;

@Repository
public interface PaymentOptionRepository extends CrudRepository<PaymentOption, UUID> {
}
