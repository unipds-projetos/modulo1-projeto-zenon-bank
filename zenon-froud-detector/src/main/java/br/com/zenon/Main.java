package br.com.zenon;
import br.com.zenon.enums.TransactionCategory;
import br.com.zenon.transactions.Transaction;
import br.com.zenon.transactions.TransactionCustumer;

import java.math.BigDecimal;


public class Main {

    public static void main (String[] args){

        Transaction transacao1 = new Transaction(1, TransactionCategory.PAYMENT, new BigDecimal("9839.64"),
                new TransactionCustumer("C1231006815", new BigDecimal("170136.0"),new BigDecimal("160296.36")),
                new TransactionCustumer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
                false, false
        );


        Transaction transacao2 = new Transaction(243, TransactionCategory.CASH_OUT, new BigDecimal("850002.52"),
                new TransactionCustumer("C1280323807", new BigDecimal("850002.52"),new BigDecimal("0.0")),
                new TransactionCustumer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true, false
        );

        System.out.println(transacao1 + "\n" + transacao2);
    }

}
