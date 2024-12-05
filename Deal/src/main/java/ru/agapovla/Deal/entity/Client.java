package ru.agapovla.Deal.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.agapovla.Deal.enums.Gender;
import ru.agapovla.Deal.enums.MaritalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID client_id;

    private String first_name;
    private String last_name;
    private String middle_name;
    private LocalDate birth_date;
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus marital_Status;

    private Integer dependent_amount;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Passport passport;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Employment employment;

    private Integer account_number;

}
