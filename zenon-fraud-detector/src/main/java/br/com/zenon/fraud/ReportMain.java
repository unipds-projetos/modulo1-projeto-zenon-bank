package br.com.zenon.fraud;

public class ReportMain {
    void main(){
        var transactionReport = new TransactionReport();
        TransactionReport.Statistics statistics = transactionReport.generateReport("data/PS_20174392719_1491204439457_log.csv");
        IO.println(
                """
                Total de linhas: %d
                Total de fraudes: %d
                Valor total transacionado: %.2f
                """.formatted(
                        statistics.totalTransactions(),
                        statistics.totalFrauds(),
                        statistics.totalAmount()
                )
        );
    }
}
