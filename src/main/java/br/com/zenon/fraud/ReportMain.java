package br.com.zenon.fraud;

import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import br.com.zenon.fraud.security.MethodGuard;

public final class ReportMain {

    private static final String PAYSIM = "data/PS_20174392719_1491204439457_log.csv";

    private ReportMain() {
    }

    public static void main(String[] args) throws IOException {
        run(PAYSIM, language(args), System.out);
    }

    static void run(String fileName, PrintStream out) throws IOException {
        run(fileName, Locale.of("pt", "BR"), out);
    }

    static void run(String fileName, Locale locale, PrintStream out) throws IOException {
        MethodGuard.notNull(locale, "locale");
        MethodGuard.notNull(out, "out");
        if (!Locale.of("pt", "BR").equals(locale) && !Locale.US.equals(locale)) {
            throw new IllegalArgumentException("language must be pt or en");
        }

        TransactionReport report = TransactionReport.read(fileName);
        ResourceBundle messages = ResourceBundle.getBundle("report", locale);
        NumberFormat currency = NumberFormat.getCurrencyInstance(locale);
        out.println(messages.getString("lines") + ": " + report.lineCount());
        out.println(messages.getString("frauds") + ": " + report.fraudCount());
        out.println(messages.getString("total") + ": " + currency.format(report.totalAmount()));
    }

    static Locale language(String[] args) {
        if (args == null || args.length > 1) {
            throw new IllegalArgumentException("language must be pt or en");
        }
        if (args.length == 0) {
            return Locale.of("pt", "BR");
        }
        return switch (args[0]) {
            case "pt" -> Locale.of("pt", "BR");
            case "en" -> Locale.US;
            default -> throw new IllegalArgumentException("language must be pt or en");
        };
    }
}
