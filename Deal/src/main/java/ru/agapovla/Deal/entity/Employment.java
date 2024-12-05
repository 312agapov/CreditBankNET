package ru.agapovla.Deal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.agapovla.Deal.enums.EmploymentPosition;
import ru.agapovla.Deal.enums.EmploymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class Employment {
    private UUID employmentId;
    private EmploymentStatus employmentStatus;
    private String employerInn;
    private BigDecimal salary;
    private EmploymentPosition employmentPosition;
    private Integer work_experienceTotal;
    private Integer work_experienceCurrent;
}
