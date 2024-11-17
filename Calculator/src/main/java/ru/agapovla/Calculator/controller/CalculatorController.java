package ru.agapovla.Calculator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.agapovla.Calculator.dto.CreditDto;
import ru.agapovla.Calculator.dto.LoanOfferDto;
import ru.agapovla.Calculator.dto.LoanStatementRequestDto;
import ru.agapovla.Calculator.dto.ScoringDataDto;
import ru.agapovla.Calculator.service.CalculatorService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/calculator")
public class CalculatorController {
    @Autowired
    private CalculatorService calculatorService;

    @PostMapping(path = "/offers")
    public List<LoanOfferDto> calculatePossibleOffers(@RequestBody LoanStatementRequestDto loanStatementRequestDto){
        return calculatorService.preScore(loanStatementRequestDto);
    }

    @PostMapping(path = "/calc")
    public CreditDto calculateOffersFull(@RequestBody ScoringDataDto scoringDataDto){
        CreditDto cred = new CreditDto();
        return cred;
    }
}
