package tiketihub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "payment_option")
public class PaymentOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID payment_option_id;
    private String optionName;
}
