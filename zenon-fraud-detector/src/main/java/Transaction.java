import java.math.BigDecimal;

public record Transaction(
        int step,
        TransactionType type,
        BigDecimal amount,
        Customer origin,
        Customer destination,
        boolean isFraud,
        boolean isFlaggedFraud
){
    public Transaction {
        if (step < 1) throw new IllegalArgumentException("step deve ser maior ou igual que 1: " + step);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("amount deve ser igual ou maior que zero: " + amount);
        if (origin == null || destination == null)
            throw new IllegalArgumentException("nenhum valor pode ser nulo");
    }
}
