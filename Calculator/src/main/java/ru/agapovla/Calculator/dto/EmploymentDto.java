package ru.agapovla.Calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Calculator.enums.EmploymentStatus;
import ru.agapovla.Calculator.enums.Position;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDto {
    public EmploymentStatus employmentStatus;
    public String employerINN;
    public BigDecimal salary;
    public Position position;
    public Integer workExperienceTotal;
    public Integer workExperienceCurrent;
}
