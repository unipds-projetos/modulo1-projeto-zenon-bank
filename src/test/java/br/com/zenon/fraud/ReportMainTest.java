package br.com.zenon.fraud;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportMainTest {

    private static final String HEADER = "step,type,amount,nameOrig,oldbalanceOrg,newbalanceOrig,"
            + "nameDest,oldbalanceDest,newbalanceDest,isFraud,isFlaggedFraud";
    private static final BigDecimal TOTAL = new BigDecimal("10000012.25");

    @Test
    void printsPortugueseByDefaultAndEnglishWhenAsked() throws Exception {
        try (DataCsv csv = DataCsv.write(HEADER + "\n" + """
                1,PAYMENT,10.00,C1,10.00,0.00,M1,0.00,0.00,0,0
                1,CASH_OUT,1.0E7,C2,1.0E7,0.00,C3,0.00,1.0E7,1,0
                1,DEBIT,2.25,C4,5.00,2.75,C5,0.00,2.25,0,1
                """)) {
            String portuguese = render(csv, Locale.of("pt", "BR"));
            String english = render(csv, Locale.US);

            assertEquals(expected(Locale.of("pt", "BR")), portuguese);
            assertEquals(expected(Locale.US), english);
            assertTrue(portuguese.contains("Valor total: R$"));
            assertTrue(portuguese.contains("10.000.012,25"));
            assertTrue(english.contains("Total value: $10,000,012.25"));
        }
    }

    @Test
    void acceptsOnlyPortugueseOrEnglish() {
        assertEquals(Locale.of("pt", "BR"), ReportMain.language(new String[0]));
        assertEquals(Locale.US, ReportMain.language(new String[] {"en"}));
        assertEquals(Locale.of("pt", "BR"), ReportMain.language(new String[] {"pt"}));

        assertEquals(
                "language must be pt or en",
                assertThrows(IllegalArgumentException.class, () -> ReportMain.language(null)).getMessage());
        assertEquals(
                "language must be pt or en",
                assertThrows(IllegalArgumentException.class, () -> ReportMain.language(new String[] {"fr"})).getMessage());
        assertEquals(
                "language must be pt or en",
                assertThrows(IllegalArgumentException.class,
                        () -> ReportMain.language(new String[] {"../report"})).getMessage());
        assertEquals(
                "language must be pt or en",
                assertThrows(IllegalArgumentException.class,
                        () -> ReportMain.language(new String[] {"pt", "en"})).getMessage());
    }

    private static String render(DataCsv csv, Locale locale) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ReportMain.run(csv.relativePath(), locale, new PrintStream(buffer, true, StandardCharsets.UTF_8));
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static String expected(Locale locale) {
        String money = NumberFormat.getCurrencyInstance(locale).format(TOTAL);
        ResourceBundleCheck labels = ResourceBundleCheck.of(locale);
        return """
                %s: 3
                %s: 1
                %s: %s
                """.formatted(labels.lines(), labels.frauds(), labels.total(), money);
    }

    private record ResourceBundleCheck(String lines, String frauds, String total) {
        static ResourceBundleCheck of(Locale locale) {
            if (Locale.US.equals(locale)) {
                return new ResourceBundleCheck("Total lines", "Total frauds", "Total value");
            }
            return new ResourceBundleCheck("Total de linhas", "Total de fraudes", "Valor total");
        }
    }
}
