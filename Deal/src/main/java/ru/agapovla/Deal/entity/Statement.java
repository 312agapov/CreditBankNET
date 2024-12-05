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
    private UUID statementId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus maritalStatus;

    private Timestamp creationDate;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String appliedOffer;

    private Timestamp signDate;

    private Integer sesCode;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private StatementStatusHistoryDto statusHistory;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private UUID clientId;

    @OneToOne
    @JoinColumn(name = "credit_id")
    private UUID creditId;

}
