package ru.agapovla.Deal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {
    public BigDecimal amount;
    public Integer term;
    public BigDecimal monthlyPayment;
    public BigDecimal rate;
    public BigDecimal psk;
    public Boolean isInsuranceEnabled;
    public Boolean isSalaryClient;
    public List<PaymentScheduleElementDto> paymentSchedule;
}
