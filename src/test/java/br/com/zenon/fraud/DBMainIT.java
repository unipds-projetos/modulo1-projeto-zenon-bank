package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DBMainIT {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";

    @Test
    void limitsTheLoadToTenThousandRows() {
        assertEquals(10_000, DBMain.INSERT_LIMIT);
    }

    @Test
    void insertsTheCsvAndSearchesThePaySimCustomer() throws Exception {
        assumeTrue(MysqlAvailability.isUp());
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + """
                1,PAYMENT,9839.64,C1231006815,170136.00,160296.36,M1979787155,0.00,0.00,0,0
                1,CASH_OUT,10.00,C200,10.00,0.00,C201,0.00,10.00,1,0
                """)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DBMain.run(csv.relativePath(), new PrintStream(buffer, true, StandardCharsets.UTF_8));

            String output = buffer.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Transações inseridas: 2\n"));
            assertTrue(output.contains("Tempo de inserção: "));
            assertTrue(output.contains("C1231006815"));
            assertTrue(output.contains("Transação não encontrada para o cliente C12345"));
        }
    }
}
