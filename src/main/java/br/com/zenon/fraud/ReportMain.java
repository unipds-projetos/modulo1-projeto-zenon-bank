package br.com.zenon.fraud;

import java.io.IOException;
import java.io.PrintStream;
import java.math.RoundingMode;

import br.com.zenon.fraud.security.MethodGuard;

public final class ReportMain {

    private ReportMain() {
    }

    public static void main(String[] args) throws IOException {
        MethodGuard.requireNoArguments(args);
        run("data/PS_20174392719_1491204439457_log.csv", System.out);
    }

    static void run(String fileName, PrintStream out) throws IOException {
        MethodGuard.notNull(out, "out");
        TransactionReport report = TransactionReport.read(fileName);
        out.println("Total de linhas: " + report.lineCount());
        out.println("Total de fraudes: " + report.fraudCount());
        out.println("Valor total transacionado: "
                + report.totalAmount().setScale(2, RoundingMode.HALF_UP).toPlainString());
    }
}
