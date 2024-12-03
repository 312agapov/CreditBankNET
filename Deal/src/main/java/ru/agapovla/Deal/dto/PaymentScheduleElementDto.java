package ru.agapovla.Deal.dto;

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
public class PaymentScheduleElementDto {
    public Integer number;
    public LocalDate date;
    public BigDecimal totalPayment;
    public BigDecimal interestPayment;
    public BigDecimal debtPayment;
    public BigDecimal remainingDebt;
}
