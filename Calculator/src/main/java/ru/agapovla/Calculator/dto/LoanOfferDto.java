package ru.agapovla.Calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDto {
    public UUID statementId;
    public BigDecimal requestedAmount;
    public BigDecimal totalAmount;
    public Integer term;
    public BigDecimal monthlyPayment;
    public BigDecimal rate;
    public Boolean isInsuranceEnabled;
    public Boolean isSalaryClient;
}
