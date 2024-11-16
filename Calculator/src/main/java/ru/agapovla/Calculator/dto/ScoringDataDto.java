package ru.agapovla.Calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Calculator.enums.Gender;
import ru.agapovla.Calculator.enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDto {
    public BigDecimal amount;
    public Integer term;
    public String firstName;
    public String lastName;
    public String middleName;
    public Gender gender;
    public LocalDate birthdate;
    public String passportSeries;
    public String passportNumber;
    public LocalDate passportIssueDate;
    public String passportIssueBranch;
    public MaritalStatus maritalStatus;
    public Integer dependentAmount;
    public EmploymentDto employment;
    public String accountNumber;
    public Boolean isInsuranceEnabled;
    public Boolean isSalaryClient;
}
