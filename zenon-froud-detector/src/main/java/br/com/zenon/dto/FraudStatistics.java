package br.com.zenon.dto;

import br.com.zenon.enums.TransactionCategory;
import br.com.zenon.transactions.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FraudStatistics {
    private long totalFrauds = 0;
    private BigDecimal totalPrejudice = BigDecimal.ZERO;
    private final Map<TransactionCategory, Long> fraudCountByType = new HashMap<>();

    public void accumulate(Transaction t){
        if(t.isFraud()){
            totalFrauds++;
            totalPrejudice = totalPrejudice.add(t.amount());

            fraudCountByType.put(
                    t.type(),
                    fraudCountByType.getOrDefault(t.type(), 0L) +1
            );
        }
    }

    public FraudStatistics combine(FraudStatistics other){
        this.totalFrauds += other.totalFrauds;
        this.totalPrejudice = this.totalPrejudice.add(other.totalPrejudice);
        other.fraudCountByType.forEach((key, value) ->
                this.fraudCountByType.put(key, this.fraudCountByType.getOrDefault(key, 0L) + value)
        );

        return this;
    }

    public long getTotalFrauds() { return totalFrauds; }
    public BigDecimal getTotalPrejudice() { return totalPrejudice; }
    public Map<TransactionCategory, Long> getFraudCountByType() { return fraudCountByType; }
}
