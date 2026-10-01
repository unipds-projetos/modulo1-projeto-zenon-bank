package br.com.zenon.fraud;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {
    void main(String[] args){
        String language = (args.length > 0 ? args[0] : "pt");

        var locale = Locale.of(language);
        var resourceBundle = ResourceBundle.getBundle("report", locale);

        var integerFormatter = NumberFormat.getIntegerInstance(locale);
        var currencyFormatter = DecimalFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(Currency.getInstance("USD"));

        var transactionReport = new TransactionReport();
        TransactionReport.Statistics statistics = transactionReport.generateReport("data/PS_20174392719_1491204439457_log.csv");

        String formattedTotalTransactions = integerFormatter.format(statistics.totalTransactions());
        String formattedTotalFrauds = integerFormatter.format(statistics.totalFrauds());
        String formattedTotalAmount = currencyFormatter.format(statistics.totalAmount());

        String msgtTotalTransactions = resourceBundle.getString("label.total.transactions");
        String msgTotalFrauds = resourceBundle.getString("label.total.frauds");
        String msgTotalAmount = resourceBundle.getString("label.total.amount");


        IO.println(
                """
                %s: %s
                %s: %s
                %s: %s
                """.formatted(
                        msgtTotalTransactions, formattedTotalTransactions,
                        msgTotalFrauds, formattedTotalFrauds,
                        msgTotalAmount, formattedTotalAmount
                )
        );
    }
}
