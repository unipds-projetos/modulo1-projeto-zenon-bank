package br.com.zenon;
import br.com.zenon.TransactionReport.Statistics;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {
    public static void main(String[] args) {

        TransactionReport transactionReport = new TransactionReport();
        Statistics statistics = transactionReport.genererateReport("data/PS_20174392719_1491204439457_log.csv");



        Locale locale = Locale.of("pt", "br");
        var integerFormatter = NumberFormat.getIntegerInstance(locale);
        var currencyFormatter = DecimalFormat.getCurrencyInstance(locale);
//        currencyFormatter.setCurrency(Currency.getInstance("USD"));


        String formattedTotalLines = integerFormatter.format(statistics.totalTransactions());
        String formattedTotalFrauds = integerFormatter.format(statistics.totalFrauds());
        String formattedTotalAmount = currencyFormatter.format(statistics.totalAmount());


        ResourceBundle resourceBundle = ResourceBundle.getBundle("report", locale);

        String totalTransactions = resourceBundle.getString("label.total.transactions");
        String totalFrauds = resourceBundle.getString("label.total.frauds");
        String totalAmount = resourceBundle.getString("label.total.amount");

        System.out.printf("""
        %s: %s
        %s: %s
        %s: %s
        """,totalTransactions, formattedTotalLines,
             totalFrauds,   formattedTotalFrauds,
               totalAmount ,formattedTotalAmount);

        System.out.println("--------------------------------");

        System.out.println("""
               %s: %s
               %s: %s
               %s: %s
                """.formatted(
                        totalTransactions,formattedTotalLines,
                        totalFrauds,formattedTotalFrauds,
                        totalAmount, formattedTotalAmount));


    }
}
