package com.example;

import com.example.dto.CustomerDTO;
import com.example.dto.TransactionDTO;
import com.example.enums.TransactionType;
import com.example.model.TransactionList;
import com.example.model.TransactionMap;
import com.example.repository.TransactionListRepository;
import com.example.services.TransactionIngestor;
import java.math.BigDecimal;
import java.util.List;


public class Main {
    static void main() throws Exception {

        var transaction1 = new TransactionDTO(1, TransactionType.PAYMENT, new BigDecimal("1864.28"),
                new CustomerDTO("C1666544295", new BigDecimal("21249.0"), new BigDecimal("19384.72")),
                new CustomerDTO("M2044282225", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false);


        var transaction2 = new TransactionDTO(1, TransactionType.TRANSFER, new BigDecimal("181.0"),
                new CustomerDTO("C1305486145", new BigDecimal("181.0"), new BigDecimal("0.0")),
                new CustomerDTO("C553264065", new BigDecimal("0.0"), new BigDecimal("0.0")),
                true, false);

//        IO.println(transaction1);
//        IO.println(transaction2);

        var transactionIngestor = new TransactionIngestor();
        List<TransactionDTO> transactions = transactionIngestor.readTransactions("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());
//        transactions.stream().limit(10).forEach(IO::println);

//        List<TransactionDTO> transactionsBadData = transactionIngestor.readTransactions("data/paysim_with_bad_data.csv");
//        IO.println(transactionsBadData.size());
//        transactionsBadData.forEach(IO::println);
//        var fraudAnalyzer = new FraudAnalyzer();
//        List<TransactionDTO>fraudeulentTransactions = fraudAnalyzer.analyzeFrauds(transactions);
//        IO.println("1. Total de Fraudes: " + fraudeulentTransactions.size());
//
//        IO.println("2. Top 3 Faudes de Maior Valor:");
//        fraudAnalyzer.biggestFrauds(fraudeulentTransactions).stream()
//                .map(TransactionDTO::amount)
//                .map(BigDecimal::toPlainString)
//                .limit(3)
//                .forEach(IO::println);
//
//        IO.println("3. Clientes Suspeitos:");
//        fraudAnalyzer.biggestFrauds(fraudeulentTransactions).stream()
//                .map(TransactionDTO::origin)
//                .map(CustomerDTO::name)
//                .distinct()
//                .limit(5)
//                .forEach(System.out::println);
//
//        IO.println("4. Prejuízo Total: " + fraudAnalyzer.totalLoss(fraudAnalyzer, fraudeulentTransactions));
//
//        IO.println("5. Fraudes por Tipo:");
//        fraudeulentTransactions.stream()
//                .collect(Collectors.groupingBy(TransactionDTO::type, Collectors.counting()))
//                .forEach((type, count)-> IO.println(type + ":" +count));

        String client = "C1231006815";

        compare("List", new TransactionList(transactions), client);
        compare("Map", new TransactionMap(transactions), client);
    }

    private static void compare(String label, TransactionListRepository repository, String client) {
        long ini = System.nanoTime();
        repository.getTransactionByOriginClientName(client)
                .ifPresentOrElse(
                        IO::println,
                        () -> IO.println("Transacação não encontrada para o cliente " + client));
        long fim = System.nanoTime();
        System.out.println(label + " - Tempo de busca: " + (fim - ini) + "ns");
    }

}

