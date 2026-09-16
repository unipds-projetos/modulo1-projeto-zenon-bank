package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DBMainBest {

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



        var repository = new TransactionSQLRepository();
        var transactionIngestorBest = new TransactionIngestorBest();

        long startTimeSQL = System.nanoTime();;

        transactionIngestorBest.readAsBatch("data/PS_20174392719_1491204439457_log.csv", ts::saveALL);


        System.out.println("Iniciando adicao de");


        long endTimeSQL = System.nanoTime();;
        System.out.println("tempo de ingestao no BD  em (ms): "+(endTimeSQL-startTimeSQL)/1_000_000.0);











    }
}
