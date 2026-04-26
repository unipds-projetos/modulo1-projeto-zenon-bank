public static void main(String[] args) throws Exception {

    IO.println("******************************************* Tarefa 03 **************************************************************");

    var transactionIngestor = new TransactionIngestor();
    List<Transaction> transactions = transactionIngestor.read(
            "C:\\Users\\fmmcb\\projetos\\modulo1-projeto-zenon-bank\\zenon-fraud-detector\\data\\PS_20174392719_1491204439457_log.csv"
    );
    IO.println(transactions.size());
    transactions.stream().limit(10).forEach(System.out::println);

    IO.println("******************************************* Tarefa 04 **************************************************************");

    List<Transaction> transactionsBadData = transactionIngestor.read(
            "C:\\Users\\fmmcb\\projetos\\modulo1-projeto-zenon-bank\\zenon-fraud-detector\\data\\paysim_with_bad_data.csv"
    );

// imprime a quantidade de transações válidas
    IO.println(transactionsBadData.size());

// imprime apenas as 10 primeiras transações válidas
    transactionsBadData.stream()
            .limit(10)
            .forEach(System.out::println);

    IO.println("******************************************* Tarefa 02 **************************************************************");

    // Transação 1
    Transaction t1 = new Transaction(
            1,
            TransactionType.PAYMENT,
            new BigDecimal("9839.64"),
            new Customer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
            new Customer("M1979787155", BigDecimal.ZERO, BigDecimal.ZERO),
            false,
            false
    );

    // Transação 2
    Transaction t2 = new Transaction(
            743,
            TransactionType.CASH_OUT,
            new BigDecimal("850002.52"),
            new Customer("C1280323807", new BigDecimal("850002.52"), BigDecimal.ZERO),
            new Customer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
            true,
            false
    );

    System.out.println("--- Transação 1 ---");
    System.out.println(t1);

    System.out.println("\n--- Transação 2 ---");
    System.out.println(t2);
}
