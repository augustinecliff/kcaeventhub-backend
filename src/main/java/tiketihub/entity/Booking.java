package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID booking_id;
    @ManyToMany(targetEntity = OrganizedEvent.class)
    private List<OrganizedEvent> organizedEvents;
    @ManyToOne(targetEntity = Payment.class)
    private Payment payments;

    @ManyToMany(mappedBy = "booking")
    private List<Ticket> tickets;
}
