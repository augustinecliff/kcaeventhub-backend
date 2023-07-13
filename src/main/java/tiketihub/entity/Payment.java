package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID payment_id;

    @ManyToMany(targetEntity = PaymentOption.class)
    private List<PaymentOption> paymentOptions;
    private String Amount;
    private String Payment_Status;
    private Date payment_date;
    private String transaction_id;
}
