package ru.agapovla.Calculator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.agapovla.Calculator.constants.Constants;
import ru.agapovla.Calculator.dto.*;
import ru.agapovla.Calculator.enums.EmploymentStatus;
import ru.agapovla.Calculator.enums.Gender;
import ru.agapovla.Calculator.enums.MaritalStatus;
import ru.agapovla.Calculator.enums.Position;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    public List<LoanOfferDto> preScore(LoanStatementRequestDto loanStatementRequestDto) {
        logger.info("Начат прескоринг:{}", loanStatementRequestDto);
        try {
            if (loanStatementRequestDto.getFirstName().length() < 2 || loanStatementRequestDto.getFirstName().length() > 30) {
                throw new IllegalArgumentException("Некорректное имя пользователя!");
            }
            if (loanStatementRequestDto.getLastName().length() < 2 || loanStatementRequestDto.getLastName().length() > 30) {
                throw new IllegalArgumentException("Некорректная фамилия пользователя!");
            }
            if (loanStatementRequestDto.getMiddleName() != null &&
                    (loanStatementRequestDto.getMiddleName().length() < 2 || loanStatementRequestDto.getMiddleName().length() > 30)) {
                throw new IllegalArgumentException("Некорректное отчетство пользователя!");
            }
            if (loanStatementRequestDto.getAmount().compareTo(new BigDecimal(20000)) < 0) {
                throw new IllegalArgumentException("Сумма кредита меньше 20000!");
            }
            if (loanStatementRequestDto.getTerm() <= 5) {
                throw new IllegalArgumentException("Срок кредита меньше 6 месяцев!");
            }
            if (loanStatementRequestDto.getBirthdate().isAfter(LocalDate.now().minusYears(18))) {
                throw new IllegalArgumentException("Вам меньше 18 лет!");
            }
            String emailPattern = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$";
            Boolean isEmailValid = Pattern.matches(emailPattern, loanStatementRequestDto.getEmail());
            if (!isEmailValid) {
                throw new IllegalArgumentException("Введен некорректный email!");
            }
            if (loanStatementRequestDto.getPassportSeries().length() != 4
                    || loanStatementRequestDto.getPassportNumber().length() != 6) {
                throw new IllegalArgumentException("Введены некорректные данные паспорта!");
            }
            List<LoanOfferDto> loanOffers = generateLoanOffers(loanStatementRequestDto.getAmount(), loanStatementRequestDto.getTerm());
            logger.info("Прескоринг завершен, созданные предложения: {}", loanOffers);
            return loanOffers;
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка в прескоринге: {}", e.getMessage());
            throw e;
        }
    }

    public List<LoanOfferDto> generateLoanOffers(BigDecimal requestedAmount, Integer term) {
        logger.info("Начат скоринг, requestAmount: {}, term: {}", requestedAmount, term);
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
        logger.info("Cкоринг завершен, созданные предложения: {}", loanOffers);
        return loanOffers;
    }

    private BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, boolean isSalaryClient) {
        logger.info("Начат расчет всей суммы кредита, amount: {}, isInsuranceEnabled: {}, isSalaryClient: {}",
                amount, isInsuranceEnabled, isSalaryClient);
        BigDecimal factor = BigDecimal.valueOf(1.1);
        if (isInsuranceEnabled) factor = factor.add(BigDecimal.valueOf(0.02));
        if (isSalaryClient) factor = factor.subtract(BigDecimal.valueOf(0.01));
        amount = amount.multiply(factor);
        logger.info("Расчет всей суммы кредита завершен: {}", amount);
        return amount;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        logger.info("Начат расчет месячного платежа, amount: {}, term: {}, isInsuranceEnabled: {}, isSalaryClient: {}",
                amount, term, isInsuranceEnabled, isSalaryClient);
        BigDecimal total = calculateTotalAmount(amount, isInsuranceEnabled, isSalaryClient);
        total = total.divide(BigDecimal.valueOf(term), BigDecimal.ROUND_HALF_UP);
        logger.info("Расчет месячного платежа завершен: {}", total);
        return total;
    }

    private BigDecimal calculateRate(boolean isInsuranceEnabled, boolean isSalaryClient) {
        logger.info("Начат расчет месячного платежа: isInsuranceEnabled: {}, isSalaryClient: {}",
                isInsuranceEnabled, isSalaryClient);
        BigDecimal baseRate = BigDecimal.valueOf(10);
        if (isInsuranceEnabled) baseRate = baseRate.subtract(BigDecimal.valueOf(2));
        if (isSalaryClient) baseRate = baseRate.subtract(BigDecimal.valueOf(1));
        logger.info("Расчет месячного платежа завершен: {}", baseRate);
        return baseRate;
    }

    private List<PaymentScheduleElementDto> createPaymentSchedule(BigDecimal amount, BigDecimal rate, Integer term,
                                                                  BigDecimal monthlyPayment) {
        logger.info("Начато создание расписания платежей: amount: {}, rate: {}, term: {}, monthlyPayment: {}",
                amount, rate, term, monthlyPayment);
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        BigDecimal remainingDebt = amount;
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL32);
        for (int i = 1; i <= term; i++) {
            BigDecimal interestPayment = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            if (i == term) {
                debtPayment = remainingDebt;
                monthlyPayment = interestPayment.add(debtPayment);
            }
            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
            PaymentScheduleElementDto element = new PaymentScheduleElementDto();
            element.setNumber(i);
            element.setDate(LocalDate.now().plusMonths(i));
            element.setTotalPayment(monthlyPayment);
            element.setInterestPayment(interestPayment);
            element.setDebtPayment(debtPayment);
            element.setRemainingDebt(remainingDebt);
            paymentSchedule.add(element);
        }
        logger.info("Расчет расписания платежей завершен: {}", paymentSchedule);
        return paymentSchedule;
    }

    public CreditDto completeScore(ScoringDataDto scoringDataDto) {
        logger.info("Начато создание итогового скоринга: scoringDataDto: {}", scoringDataDto);
        BigDecimal overAllTerm = BigDecimal.valueOf(1.1);
        try {
            if (scoringDataDto.getEmployment().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED)) {
                throw new IllegalArgumentException("Отказ по трудоустройству!");
            }
            if (scoringDataDto.getEmployment().getEmploymentStatus().equals(EmploymentStatus.EMPLOYED)) {
                overAllTerm = overAllTerm.add(BigDecimal.valueOf(1));
            }
            if (scoringDataDto.getEmployment().getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED)) {
                overAllTerm = overAllTerm.add(BigDecimal.valueOf(2));
            }
            if (scoringDataDto.getEmployment().getPosition().equals(Position.DIRECTOR)
                    || scoringDataDto.getEmployment().getPosition().equals(Position.MANAGER)) {
                overAllTerm = overAllTerm.add(BigDecimal.valueOf(1));
            }
            if (scoringDataDto.getEmployment().getPosition().equals(Position.WORKER)
                    || scoringDataDto.getEmployment().getPosition().equals(Position.ENGINEER)) {
                overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(1));
            }
            if (scoringDataDto.getAmount().compareTo(scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(24))) >= 0) {
                throw new IllegalArgumentException("Отказ по сумме кредита!");
            }
            if (scoringDataDto.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
                overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(3));
            } else {
                overAllTerm = overAllTerm.add(BigDecimal.valueOf(1));
            }
            if (scoringDataDto.getBirthdate().isAfter(LocalDate.now().minusYears(20))
                    && !scoringDataDto.getBirthdate().isBefore(LocalDate.now().minusYears(65))) {
                throw new IllegalArgumentException("Отказ по возрасту!");
            }
            if (scoringDataDto.getGender().equals(Gender.FEMALE)
                    && scoringDataDto.getBirthdate().isBefore(LocalDate.now().minusYears(32))
                    && !scoringDataDto.getBirthdate().isAfter(LocalDate.now().minusYears(60))) {
                overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(3));
            }
            if (scoringDataDto.getGender().equals(Gender.MALE)
                    && scoringDataDto.getBirthdate().isBefore(LocalDate.now().minusYears(30))
                    && !scoringDataDto.getBirthdate().isAfter(LocalDate.now().minusYears(55))) {
                overAllTerm = overAllTerm.subtract(BigDecimal.valueOf(3));
            }
            if (!scoringDataDto.getGender().equals(Gender.FEMALE) && !scoringDataDto.getGender().equals(Gender.MALE)) {
                overAllTerm = overAllTerm.add(Constants.NON_BINARY_PERCENT);
            }
            if (scoringDataDto.getEmployment().getWorkExperienceTotal() < 18
                    || scoringDataDto.getEmployment().getWorkExperienceCurrent() < 3) {
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
            finalCreditOffer.setPaymentSchedule(createPaymentSchedule(
                    scoringDataDto.getAmount(),
                    finalCreditOffer.getRate(),
                    scoringDataDto.getTerm(),
                    finalCreditOffer.getMonthlyPayment()));
            logger.info("Итоговый скоринг завершен: finalCreditOffer: {}", finalCreditOffer);
            return finalCreditOffer;
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка в итоговом скоринге: {}", e.getMessage());
            throw e;
        }

    }
}
