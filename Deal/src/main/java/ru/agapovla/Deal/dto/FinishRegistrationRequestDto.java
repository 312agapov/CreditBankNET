package ru.agapovla.Deal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Deal.enums.Gender;
import ru.agapovla.Deal.enums.MaritalStatus;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinishRegistrationRequestDto {
    public Gender gender;
    public MaritalStatus maritalStatus;
    public Integer dependentAmount;
    public LocalDate passportIssueDate;
    public String passportIssueBranch;
    public EmploymentDto employment;
    public String accountNumber;
}
