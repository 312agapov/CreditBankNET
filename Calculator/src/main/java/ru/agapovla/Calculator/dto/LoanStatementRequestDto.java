package ru.agapovla.Calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatementRequestDto {
    private BigDecimal amount; //сумма
    private Integer term; //срок
    private String firstName;
    private String lastName;
    private String middleName; //фио
    private String email;
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;
}
