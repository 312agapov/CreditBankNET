package ru.agapovla.Calculator.service;

import org.springframework.stereotype.Service;
import ru.agapovla.Calculator.dto.CreditDto;
import ru.agapovla.Calculator.dto.LoanOfferDto;
import ru.agapovla.Calculator.dto.LoanStatementRequestDto;
import ru.agapovla.Calculator.dto.ScoringDataDto;
import ru.agapovla.Calculator.enums.EmploymentStatus;
import ru.agapovla.Calculator.enums.Gender;
import ru.agapovla.Calculator.enums.MaritalStatus;
import ru.agapovla.Calculator.enums.Position;

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
        if (isInsuranceEnabled) baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        if (isSalaryClient) baseRate = baseRate.subtract(BigDecimal.valueOf(1));
        return baseRate;
    }

    public CreditDto completeScore(ScoringDataDto scoringDataDto){
        BigDecimal overAllTerm = BigDecimal.valueOf(1.1);
        if(scoringDataDto.getEmployment().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED)){
            throw new IllegalArgumentException("Отказ по трудоустройству!");
        }
        if(scoringDataDto.getEmployment().getEmploymentStatus().equals(EmploymentStatus.EMPLOYED)){
            overAllTerm = overAllTerm.add(BigDecimal.valueOf(1));
        }
        if(scoringDataDto.getEmployment().getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED)){
            overAllTerm = overAllTerm.add(BigDecimal.valueOf(2));
        }
        if(scoringDataDto.getEmployment().getPosition().equals(Position.DIRECTOR)
                || scoringDataDto.getEmployment().getPosition().equals(Position.MANAGER)){
            overAllTerm = overAllTerm.add(BigDecimal.valueOf(1));
        }
        if(scoringDataDto.getEmployment().getPosition().equals(Position.WORKER)
                || scoringDataDto.getEmployment().getPosition().equals(Position.ENGINEER)){
            overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(1));
        }
        if(scoringDataDto.getAmount().compareTo(scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) >= 0){
            throw new IllegalArgumentException("Отказ по сумме кредита!");
        }
        if(scoringDataDto.getMaritalStatus().equals(MaritalStatus.MARRIED)){
            overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(3));
        } else {
            overAllTerm = overAllTerm.add(BigDecimal.valueOf(1));
        }
        if(scoringDataDto.getBirthdate().isAfter(LocalDate.now().minusYears(20))
                && !scoringDataDto.getBirthdate().isBefore(LocalDate.now().minusYears(65))){
            throw new IllegalArgumentException("Отказ по возрасту!");
        }
        if(scoringDataDto.getGender().equals(Gender.FEMALE)
                && scoringDataDto.getBirthdate().isBefore(LocalDate.now().minusYears(32))
                && !scoringDataDto.getBirthdate().isAfter(LocalDate.now().minusYears(60))){
            overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(3));
        }
        if(scoringDataDto.getGender().equals(Gender.MALE)
                && scoringDataDto.getBirthdate().isBefore(LocalDate.now().minusYears(30))
                && !scoringDataDto.getBirthdate().isAfter(LocalDate.now().minusYears(55))){
            overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(3));
        }
        if(!scoringDataDto.getGender().equals(Gender.FEMALE) && !scoringDataDto.getGender().equals(Gender.MALE)){
            overAllTerm = overAllTerm.add(BigDecimal.valueOf(10));
        }
        if(scoringDataDto.getEmployment().getWorkExperienceTotal() < 18
                || scoringDataDto.getEmployment().getWorkExperienceCurrent() < 3){
            throw new IllegalArgumentException("Отказ по стажу!");
        }
        CreditDto finalCreditOffer = new CreditDto();
        finalCreditOffer.setAmount(scoringDataDto.getAmount());
        finalCreditOffer.setTerm(overAllTerm.intValue());
        finalCreditOffer.setMonthlyPayment(calculateMonthlyPayment(
                scoringDataDto.getAmount(),
                scoringDataDto.getTerm(),
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient()));
        finalCreditOffer.setRate(calculateRate(
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient()));
        finalCreditOffer.setPsk(calculateTotalAmount(
                scoringDataDto.getAmount(),
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient()));
        finalCreditOffer.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
        finalCreditOffer.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        finalCreditOffer.setPaymentSchedule(); //TODO:
    }
}
