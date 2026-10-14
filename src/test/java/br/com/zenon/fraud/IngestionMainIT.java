package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class IngestionMainIT {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void replacesTheTableAndPrintsTheTotals() throws Exception {
        assumeTrue(MysqlAvailability.isUp());
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + """
                1,PAYMENT,10.50,C1231006815,10.50,0.00,M1,0.00,0.00,0,0
                1,TRANSFER,2.00,C2,2.00,0.00,C3,0.00,2.00,1,0
                1,DEBIT,1.25,C4,1.25,0.00,C5,0.00,1.25,0,0
                """)) {
            String first = ingest(csv);
            String second = ingest(csv);

            assertTrue(first.contains("Transações inseridas: 3\n"));
            assertTrue(first.contains("Total de fraudes: 1\n"));
            assertTrue(first.contains("Valor total transacionado: 13.75\n"));
            assertTrue(first.contains("Tempo total: "));
            assertTrue(second.contains("Transações inseridas: 3\n"));

            try (TransactionSQLRepository repository = TransactionSQLRepository.open()) {
                DatasetTotals totals = repository.totals();
                assertEquals(3, totals.transactions());
                assertEquals(1, totals.frauds());
                assertEquals(0, totals.totalAmount().compareTo(new BigDecimal("13.75")));
            }
        }
    }

    private static String ingest(DataCsv csv) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        IngestionMain.run(csv.relativePath(), new PrintStream(buffer, true, StandardCharsets.UTF_8));
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
