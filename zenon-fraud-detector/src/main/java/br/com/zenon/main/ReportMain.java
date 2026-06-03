package br.com.zenon.main;

import br.com.zenon.service.TransactionReport;
import br.com.zenon.service.TransactionReport.Statistics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {
    void main(String[] args){
        String language = (args.length > 0) ? args[0] : "pt";
        var locale = Locale.of(language);

        var integerFormatter = NumberFormat.getIntegerInstance(locale);
        var currencyFormatted = DecimalFormat.getCurrencyInstance();
        currencyFormatted.setCurrency(Currency.getInstance("USD"));

        var resourceBundle = ResourceBundle.getBundle("report", locale);

        var transactionReport = new TransactionReport();
        Statistics statistics = transactionReport.generateReport("data/PS_20174392719_1491204439457_log.csv");

        String fmtTotalTransactions = integerFormatter.format(statistics.totalTransactions());
        String fmtFraudsFormatted = integerFormatter.format(statistics.totalFrauds());
        String fmtAmountFormatted = currencyFormatted.format(statistics.totalAmount());

        String msgTotalTransactions = resourceBundle.getString("label.total.transactions");
        String msgTotalFrauds = resourceBundle.getString("label.total.frauds");
        String msgTotalAmount = resourceBundle.getString("label.total.amount");


        IO.println("""
                %s: %s
                %s: %s
                %s: %s
                """.formatted(
                        msgTotalTransactions,fmtTotalTransactions,
                        msgTotalFrauds,fmtFraudsFormatted,
                        msgTotalAmount,fmtAmountFormatted));
    }
}
