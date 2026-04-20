import br.com.zenon.fraud.EnumTransactionType;
import br.com.zenon.fraud.FraudAnalyzer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionIngestor;

import java.util.List;
import java.util.Map;

class Principal {
    public static void main(String[] args) {
        System.out.println("Iniciando....");
        TransactionIngestor transactionIngestor = new TransactionIngestor();
        //List<Transaction> lista = transactionIngestor.readOld("data/PS_20174392719_1491204439457_log.csv");
        List<Transaction> lista = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        //List<Transaction> lista = transactionIngestor.read("data/paysim_with_bad_data.csv");

        System.out.println("Quantidade de transações lidas: " + lista.size());
        //lista.forEach(System.out::println);

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer(lista);

        System.out.println("1. Total de Fraudes: " + fraudAnalyzer.countFrauds());
        System.out.println("2. Top 3 Fraudes de Maior Valor: ");
        fraudAnalyzer.findTopAmounts(3).stream().forEach(item -> System.out.println(item.amount().toPlainString()));
        System.out.println("3. Clientes Suspeitos: ");
        fraudAnalyzer.findTopSuspiciousClients(5).stream().forEach(System.out::println);
        System.out.println("4. Prejuizo Total: " + fraudAnalyzer.prejuzioTotal(lista).toPlainString());
        System.out.println("5. Prejuizo Por Tipo: ");
        Map<EnumTransactionType, Long> map = fraudAnalyzer.quantidadePorTipo();
        map.forEach((k, v) -> System.out.println(" - " + k + ": " + v));
    }
}
