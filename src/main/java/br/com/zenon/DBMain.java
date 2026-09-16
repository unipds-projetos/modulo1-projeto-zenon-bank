package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DBMain {

    public static void main(String[] args) {


        ConnectionFactory.getConnection();
        System.out.println("Conexao com BD criada!");


        TransactionSQLRepository ts = new TransactionSQLRepository();
//         Optional<Transaction> result = ts.findByOriginName("C1000002");
//
//         result.ifPresentOrElse(System.out::println,
//                 ()->System.out.println("transactiOn not found"));

//         TransactionCostumer origin = new TransactionCostumer("mikhael", new BigDecimal("150000000.00"), new BigDecimal("75000000.00"));
//         TransactionCostumer recipient = new TransactionCostumer("mourao", new BigDecimal("0.0"), new BigDecimal("75000000.00"));
//
//         Transaction t = new Transaction(2, TransactionType.DEBIT, new BigDecimal("999999999999.00"),origin, recipient, true, true);
//
//         ts.save(t);


        List<Transaction> transactions = new TransactionIngestor().readNew("data/PS_20174392719_1491204439457_log.csv");


        long startTimeSQL = System.nanoTime();;

        System.out.println("SIZE transactions: "+transactions.size());
        System.out.println("Iniciando adicao de");

        ts.saveALL(transactions);


        long endTimeSQL = System.nanoTime();;
        System.out.println("tempo de busca LIST  em (ms): "+(endTimeSQL-startTimeSQL)/1_000_000.0);


       ts.findByOriginName("C1231006815")
                .ifPresentOrElse(System.out::println, ()-> System.out.println("transacao nao encontrada: C1231006815"));





    }
}
