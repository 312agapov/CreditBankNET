package ru.agapovla.Deal.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.agapovla.Deal.dto.PaymentScheduleElementDto;
import ru.agapovla.Deal.enums.CreditStatus;
import ru.agapovla.Deal.enums.Gender;
import ru.agapovla.Deal.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID credit_id;

    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthly_payment;
    private BigDecimal rate;
    private BigDecimal psk;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private PaymentScheduleElementDto payment_schedule;

    private Boolean insurance_enabled;
    private Boolean is_salary_client;
    private CreditStatus credit_status;
}
