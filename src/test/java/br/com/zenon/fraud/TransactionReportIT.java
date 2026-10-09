package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class TransactionReportIT {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void printsTheSummaryOfAMultiRowFile() throws Exception {
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + """
                1,PAYMENT,10.00,C1,10.00,0.00,M1,0.00,0.00,0,0
                1,CASH_OUT,1.0E7,C2,1.0E7,0.00,C3,0.00,1.0E7,1,0
                1,DEBIT,2.25,C4,5.00,2.75,C5,0.00,2.25,0,1
                """)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ReportMain.run(csv.relativePath(), new PrintStream(buffer, true, StandardCharsets.UTF_8));

            String output = buffer.toString(StandardCharsets.UTF_8);
            assertTrue(output.startsWith("Total de linhas: 3\nTotal de fraudes: 1\nValor total: "));
            assertTrue(output.contains("10.000.012,25"));
        }
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.MINUTES)
    void summarizesTheOriginalPaySimFileWhenItIsPresent() throws Exception {
        Path csv = Path.of("data/PS_20174392719_1491204439457_log.csv");
        assumeTrue(Files.isRegularFile(csv), "dataset PaySim ausente");

        TransactionReport report = TransactionReport.read("data/PS_20174392719_1491204439457_log.csv");

        assertEquals(6_362_620, report.lineCount());
        assertEquals(8_213, report.fraudCount());
        assertEquals(new BigDecimal("1144392944759.77"), report.totalAmount());
    }
}
