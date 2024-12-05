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
    private UUID employment_id;
    private EmploymentStatus employment_status;
    private String employer_inn;
    private BigDecimal salary;
    private EmploymentPosition employmentPosition;
    private Integer work_experience_total;
    private Integer work_experience_current;
}
