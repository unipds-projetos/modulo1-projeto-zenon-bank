package br.com.zenon;
import br.com.zenon.transactions.Transaction;
import br.com.zenon.transactions.TransactionIngetor;

import java.util.List;


public class Main {

    public static void main (String[] args){
        String paySimFile = "../data/PS_20174392719_1491204439457_log.csv";

        TransactionIngetor transactionIngetor = new TransactionIngetor();

        List<Transaction> transactions = transactionIngetor.read(paySimFile);

        transactions.stream().limit(10).forEach(IO::println);

    }

}
