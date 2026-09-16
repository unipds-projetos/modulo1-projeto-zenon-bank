package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;



public class Main {
    public static void main(String[] args) {


        
        List<Transaction> transactions = new TransactionIngestor().readNew("data/PS_20174392719_1491204439457_log.csv");
        long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("Memória máxima da JVM: "
                + maxMemory / 1024 / 1024 + " MB");

        System.out.println("JVM args: "
                + java.lang.management.ManagementFactory
                .getRuntimeMXBean()
                .getInputArguments());


        System.out.println("SIZE original transactions:"+transactions.size());
//        transactions.stream().limit(10).forEach(System.out::println);


        List<Transaction> transactionsBadData = new TransactionIngestor().readNew("data/paysim_with_bad_data.csv");

        System.out.println("SIZE bad transactions: "+transactionsBadData.size());
        transactionsBadData.forEach(System.out::println);

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(transactions);
        long fraudCounter = fraudAnalyzer.countFrauds();

        List<Transaction> top3Frauds = fraudAnalyzer.getTopFrauds(3);
        System.out.println("TOTAL OF FRAUDS: "+ fraudCounter );
        System.out.println("TOP 3 FRAUDS: ");
        top3Frauds.stream().map(t->t.amount()).forEach(amount-> System.out.printf("- %.2f%n",amount));

        List<String> top5Suspects = fraudAnalyzer.findSuspects(5);
        System.out.println("TOP 5 SUSPECTS FRAUDS: "+top5Suspects);

        BigDecimal totalDamage = fraudAnalyzer.totalDamage();
        System.out.println("TOTAL DAMAGE: "+totalDamage);

        Double totalDamage2 = fraudAnalyzer.totalDamage2();
        System.out.println("TOTAL DAMAGE2: "+totalDamage2);

        Map<TransactionType, Long> transactionTypeMap = fraudAnalyzer.countFraudsByType();
        System.out.println("TOTAL TRANSACTION TYPE: "+transactionTypeMap);

        transactionTypeMap.forEach((type, count)-> System.out.println("-%s: %d".formatted(type, count)));

        transactionTypeMap.forEach("-%s: %d"::formatted);

        TransactionRepository transactionRepository;

        transactionRepository = new TransactionListRepository(transactions);
        String founfOriginName  = "C1868032458";

        long startTimeList = System.nanoTime();;

        transactionRepository.findByOriginName(founfOriginName)
                .ifPresentOrElse(System.out::println, ()-> System.out.println("transacao nao encontrada: "+founfOriginName));

        long endTimeList = System.nanoTime();;
        System.out.println("tempo de busca LIST  em (ms): "+(endTimeList-startTimeList)/1_000_000.0);


        transactionRepository = new TransactionMapRepository(transactions);



        long startTimeList2 = System.nanoTime();;

        transactionRepository.findByOriginName(founfOriginName)
                .ifPresentOrElse(System.out::println, ()-> System.out.println("transacao nao encontrada: "+founfOriginName));

        long endTimeList2 = System.nanoTime();;
        System.out.println("tempo de busca MAP  em (ms): "+(endTimeList2-startTimeList2)/1_000_000.0);




    }
}