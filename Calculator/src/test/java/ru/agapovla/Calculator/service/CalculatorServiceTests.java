package ru.agapovla.Calculator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.agapovla.Calculator.dto.*;
import ru.agapovla.Calculator.enums.EmploymentStatus;
import ru.agapovla.Calculator.enums.Gender;
import ru.agapovla.Calculator.enums.MaritalStatus;
import ru.agapovla.Calculator.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTests {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    @Test
    void preScore_validRequest_shouldReturnLoanOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Иван");
        request.setLastName("Иванов");
        request.setMiddleName("Иванович");
        request.setAmount(new BigDecimal("30000"));
        request.setTerm(12);
        request.setBirthdate(LocalDate.now().minusYears(25));
        request.setEmail("ivan.ivanov@example.com");
        request.setPassportSeries("1234");
        request.setPassportNumber("567890");

        List<LoanOfferDto> loanOffers = calculatorService.preScore(request);

        assertNotNull(loanOffers);
        assertEquals(4, loanOffers.size(), "Должно быть 4 предложения кредита");
        assertTrue(loanOffers.stream().allMatch(o -> o.getRequestedAmount().equals(request.getAmount())));
    }

    @Test
    void preScore_invalidEmail_shouldThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Иван");
        request.setLastName("Иванов");
        request.setMiddleName("Иванович");
        request.setAmount(new BigDecimal("30000"));
        request.setTerm(12);
        request.setBirthdate(LocalDate.now().minusYears(25));
        request.setEmail("invalid-email");
        request.setPassportSeries("1234");
        request.setPassportNumber("567890");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculatorService.preScore(request));
        assertEquals("Введен некорректный email!", exception.getMessage());
    }

    @Test
    void generateLoanOffers_validParameters_shouldReturnSortedOffers() {
        BigDecimal amount = new BigDecimal("30000");
        int term = 12;

        List<LoanOfferDto> loanOffers = calculatorService.generateLoanOffers(amount, term);

        assertNotNull(loanOffers);
        assertEquals(4, loanOffers.size());
        assertTrue(loanOffers.get(0).getRate().compareTo(loanOffers.get(1).getRate()) <= 0, "Предложения должны быть отсортированы по ставке");
    }

    @Test
    void completeScore_validData_shouldReturnFinalOffer() {
        ScoringDataDto scoringData = new ScoringDataDto();
        scoringData.setAmount(new BigDecimal("50000"));
        scoringData.setTerm(24);
        scoringData.setBirthdate(LocalDate.now().minusYears(30));
        scoringData.setIsInsuranceEnabled(true);
        scoringData.setIsSalaryClient(true);
        scoringData.setMaritalStatus(MaritalStatus.SINGLE);
        scoringData.setGender(Gender.FEMALE);
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setPosition(Position.WORKER);
        employment.setSalary(new BigDecimal("30000"));
        employment.setWorkExperienceTotal(24);
        employment.setWorkExperienceCurrent(12);
        scoringData.setEmployment(employment);

        CreditDto creditOffer = calculatorService.completeScore(scoringData);

        assertNotNull(creditOffer);
        assertTrue(creditOffer.getRate().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(scoringData.getAmount(), creditOffer.getAmount());
        assertEquals(26, creditOffer.getTerm());
    }
}