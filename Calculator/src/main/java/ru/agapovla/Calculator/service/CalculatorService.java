package ru.agapovla.Calculator.service;

import org.springframework.stereotype.Service;
import ru.agapovla.Calculator.dto.LoanOfferDto;
import ru.agapovla.Calculator.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CalculatorService {
    public List<LoanOfferDto> preScore(LoanStatementRequestDto loanStatementRequestDto){
        if(loanStatementRequestDto.getFirstName().length() < 2 || loanStatementRequestDto.getFirstName().length() > 30){
            throw new IllegalArgumentException("Некорректное имя пользователя!");
        }
        if(loanStatementRequestDto.getLastName().length() < 2 || loanStatementRequestDto.getLastName().length() > 30){
            throw new IllegalArgumentException("Некорректная фамилия пользователя!");
        }
        if(loanStatementRequestDto.getMiddleName() != null &&
                (loanStatementRequestDto.getMiddleName().length() < 2 || loanStatementRequestDto.getMiddleName().length() > 30)){
            throw new IllegalArgumentException("Некорректное отчетство пользователя!");
        }
        if(loanStatementRequestDto.getAmount().compareTo(new BigDecimal(20000)) < 0){
            throw new IllegalArgumentException("Сумма кредита меньше 20000!");
        }
        if(loanStatementRequestDto.getTerm() <= 5){
            throw new IllegalArgumentException("Срок кредита меньше 6 месяцев!");
        }
        if(loanStatementRequestDto.getBirthdate().isAfter(LocalDate.now().minusYears(18))){
            throw new IllegalArgumentException("Вам меньше 18 лет!");
        }
        String emailPattern = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$";
        Boolean isEmailValid = Pattern.matches(emailPattern,loanStatementRequestDto.getEmail());
        if(!isEmailValid){
            throw new IllegalArgumentException("Введен некорректный email!");
        }
        if(loanStatementRequestDto.getPassportSeries().length() != 4
                || loanStatementRequestDto.getPassportNumber().length() != 6){
            throw new IllegalArgumentException("Введены некорректные данные паспорта!");
        }
        return generateLoanOffers(loanStatementRequestDto.getAmount(), loanStatementRequestDto.getTerm());
    }

    public List<LoanOfferDto> generateLoanOffers(BigDecimal requestedAmount, Integer term) {
        List<LoanOfferDto> loanOffers = new ArrayList<>();
        boolean[] booleanValues = {false, true};
        for (boolean isInsuranceEnabled : booleanValues) {
            for (boolean isSalaryClient : booleanValues) {
                LoanOfferDto loanOffer = LoanOfferDto.builder()
                        .statementId(UUID.randomUUID())
                        .requestedAmount(requestedAmount)
                        .totalAmount(calculateTotalAmount(requestedAmount, isInsuranceEnabled, isSalaryClient))
                        .term(term)
                        .monthlyPayment(calculateMonthlyPayment(requestedAmount, term, isInsuranceEnabled, isSalaryClient))
                        .rate(calculateRate(isInsuranceEnabled, isSalaryClient))
                        .isInsuranceEnabled(isInsuranceEnabled)
                        .isSalaryClient(isSalaryClient)
                        .build();
                loanOffers.add(loanOffer);
            }
        }
        loanOffers = loanOffers.stream()
                .sorted(Comparator.comparing(LoanOfferDto::getRate))
                .collect(Collectors.toList());
        return loanOffers;
    }

    private BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, boolean isSalaryClient) {
        BigDecimal factor = BigDecimal.valueOf(1.1);
        if (isInsuranceEnabled) factor = factor.add(BigDecimal.valueOf(0.02));
        if (isSalaryClient) factor = factor.subtract(BigDecimal.valueOf(0.01));
        return amount.multiply(factor);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        BigDecimal total = calculateTotalAmount(amount, isInsuranceEnabled, isSalaryClient);
        return total.divide(BigDecimal.valueOf(term), BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        BigDecimal baseRate = BigDecimal.valueOf(10);
        if (isInsuranceEnabled) baseRate = baseRate.subtract(BigDecimal.valueOf(0.5));
        if (isSalaryClient) baseRate = baseRate.subtract(BigDecimal.valueOf(1));
        return baseRate;
    }
}
