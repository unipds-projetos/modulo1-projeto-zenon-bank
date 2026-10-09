package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionReportTest {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void sumsTheWholeFileWithoutKeepingTheRows() throws Exception {
        try (DataCsv csv = DataCsv.write(HEADER + """

                1,PAYMENT,10.00,C1,10.00,0.00,M1,0.00,0.00,0,0

                1,TRANSFER,1.0E7,C2,1.0E7,0.00,C3,0.00,0.00,1,0
                1,PAYMENT,0.50,C4,1.00,0.50,M2,0.00,0.00,0,0
                """)) {
            TransactionReport report = TransactionReport.read(csv.relativePath());

            assertEquals(3, report.lineCount());
            assertEquals(1, report.fraudCount());
            assertEquals(new BigDecimal("10000010.50"), report.totalAmount());
        }
    }

    @Test
    void rejectsAPathOutsideDataAndARowThatDoesNotMatchTheSchema() throws Exception {
        assertEquals(
                "fileName must stay inside data/",
                assertThrows(IllegalArgumentException.class, () -> TransactionReport.read("../pom.xml")).getMessage());

        try (DataCsv csv = DataCsv.write("outro\n1,PAYMENT,1.00,C1,1.00,0.00,M1,0.00,0.00,0,0\n")) {
            assertEquals(
                    "csv header is not the PaySim schema",
                    assertThrows(IllegalArgumentException.class, () -> TransactionReport.read(csv.relativePath()))
                            .getMessage());
        }
    }

    @Test
    void rejectsAnOversizedOrCorruptRowWithoutRevealingIt() throws Exception {
        String secret = "X".repeat(600);
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + secret + "\n")) {
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class, () -> TransactionReport.read(csv.relativePath()));

            assertEquals("linha rejeitada (600 caracteres)", error.getMessage());
            assertFalse(error.getMessage().contains(secret));
        }

        try (DataCsv csv = DataCsv.write(HEADER + "\n1,PAYMENT,-1.00,C1,1.00,0.00,M1,0.00,0.00,0,0\n")) {
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class, () -> TransactionReport.read(csv.relativePath()));

            assertEquals("linha rejeitada (45 caracteres)", error.getMessage());
            assertFalse(error.getMessage().contains("-1.00"));
            assertFalse(error.getMessage().contains("C1"));
        }
    }

    @Test
    void rejectsAnInconsistentSummary() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionReport(-1, 0, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionReport(1, 2, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionReport(1, 0, new BigDecimal("-1")));
    }
}
