package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID ticket_id;
    @ManyToMany
    @JoinTable(
            name = "ticket_email",
            joinColumns = @JoinColumn(name = "ticket_id"),
            inverseJoinColumns = @JoinColumn(name = "email_id"))
    private List<Email> emails;

    @ManyToMany
    @JoinTable(
            name = "ticket_booking",
            joinColumns = @JoinColumn(name = "ticket_id"),
    inverseJoinColumns = @JoinColumn(name = "booking_id"))
    private List<Booking> booking;
    private String ticket_status;
    private Date date_of_issue;
    private Date expiry_date;

}
