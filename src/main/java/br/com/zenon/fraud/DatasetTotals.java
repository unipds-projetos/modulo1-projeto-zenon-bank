package br.com.zenon.fraud;

import java.math.BigDecimal;

public record DatasetTotals(long transactions, long frauds, BigDecimal totalAmount) {

    public DatasetTotals {
        if (transactions < 0 || frauds < 0 || frauds > transactions) {
            throw new IllegalArgumentException("totals are invalid");
        }
        if (totalAmount == null || totalAmount.signum() < 0) {
            throw new IllegalArgumentException("totalAmount is invalid");
        }
    }
}
