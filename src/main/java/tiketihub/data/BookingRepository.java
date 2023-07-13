package tiketihub.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tiketihub.entity.Booking;

import java.util.UUID;


@Repository
public interface BookingRepository extends CrudRepository<Booking, UUID> {
}
