package com.example.services;


import com.example.dto.TransactionDTO;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class FraudAnalyzer {


    public static List<TransactionDTO> analyzeFrauds(List<TransactionDTO> transactions) {
        return transactions.stream()
                .filter(TransactionDTO::isFraud)
                .toList();
    }

    public static List<TransactionDTO> biggestFrauds(List<TransactionDTO> fraudeulentTransactions) {
        return fraudeulentTransactions.stream()
                .sorted(Comparator.comparing(TransactionDTO::amount).reversed())
                .toList();
    }

    public static BigDecimal totalLoss(FraudAnalyzer fraudAnalyzer, List<TransactionDTO> fraudeulentTransactions) {
        return  fraudAnalyzer.biggestFrauds(fraudeulentTransactions).stream()
                .map(TransactionDTO::amount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
