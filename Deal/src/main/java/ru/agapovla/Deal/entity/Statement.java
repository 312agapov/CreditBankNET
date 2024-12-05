package ru.agapovla.Deal.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.agapovla.Deal.dto.StatementStatusHistoryDto;
import ru.agapovla.Deal.enums.ApplicationStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Statement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID statement_id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus marital_Status;

    private Timestamp creation_date;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String applied_offer;

    private Timestamp sign_date;

    private Integer ses_code;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private StatementStatusHistoryDto status_history;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private UUID client_id;

    @OneToOne
    @JoinColumn(name = "credit_id")
    private UUID credit_id;

}
