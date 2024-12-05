package ru.agapovla.Deal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Deal.enums.EmploymentStatus;
import ru.agapovla.Deal.enums.EmploymentPosition;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDto {
    public EmploymentStatus employmentStatus;
    public String employerINN;
    public BigDecimal salary;
    public EmploymentPosition employmentPosition;
    public Integer workExperienceTotal;
    public Integer workExperienceCurrent;
}
