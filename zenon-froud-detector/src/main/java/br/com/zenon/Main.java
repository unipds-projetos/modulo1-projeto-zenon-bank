package br.com.zenon;
import br.com.zenon.dto.FraudStatistics;
import br.com.zenon.enums.TransactionCategory;
import br.com.zenon.transactions.FraudAnalyzer;
import br.com.zenon.transactions.Transaction;
import br.com.zenon.transactions.TransactionIngetor;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Main {

    public static void main (String[] args){
        String paySimFile = "../data/PS_20174392719_1491204439457_log.csv";

        long inicio = System.nanoTime();

//        TransactionIngetor transactionIngetor = new TransactionIngetor();
//
//        List<Transaction> transactions = transactionIngetor.read(paySimFile);
//
//        transactions.stream().limit(10).forEach(IO::println);
//
//        IO.print("-----------------------------------------------------------------\n");
//
//        paySimFile = "../data/paysim_with_bad_data.csv";
//
//        List<Transaction> transactionBadData = transactionIngetor.read(paySimFile);
//        IO.println(transactionBadData.size());
//
//        transactionBadData.forEach(IO::println);
//
//        IO.print("-----------------------------------------------------------------\n");

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(paySimFile);

        FraudStatistics stats = fraudAnalyzer.calculateStatistics();

        List<BigDecimal> getTopThreeFrauds = fraudAnalyzer.TopThreeFraud();
        Set<String> flaggedCustomer = fraudAnalyzer.FlaggedCustomer();

        System.out.println("1. Total de fraudes: " + stats.getTotalFrauds());

        System.out.println("2. Top 3 Fraudes de maior valor:");
        getTopThreeFrauds.forEach(amount -> System.out.println(amount.toPlainString()));

        System.out.println("3. Clientes Suspeitos:");
        flaggedCustomer.forEach(System.out::println);

        System.out.println("4. Prejuízo total: " + stats.getTotalPrejudice().toPlainString());

        System.out.println("5. Fraudes por tipo: ");
        stats.getFraudCountByType().forEach((type, count) -> {
            System.out.println("-" + type + ": " + count);
        });

        long fim = System.nanoTime();
        long duracaoMillisegundos = (fim - inicio) / 1_000_000;
        System.out.print("Tempo de execução: " + duracaoMillisegundos + " ms");

    }

}
