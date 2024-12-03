package ru.agapovla.Calculator.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanOfferDto {
    public UUID statementId;
    public BigDecimal requestedAmount; //
    public BigDecimal totalAmount;
    public Integer term; //срок
    public BigDecimal monthlyPayment; //платеж в месяц
    public BigDecimal rate; //ставка
    public Boolean isInsuranceEnabled; //есть ли страховка
    public Boolean isSalaryClient; //работает ли
}
