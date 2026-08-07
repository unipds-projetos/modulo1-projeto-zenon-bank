package br.com.zenon;
import java.math.BigDecimal;

public record Transaction(
          int step,
          TransactionType type,
          BigDecimal amount,
          Customer origin,
          Customer destination,
          boolean fraud,
          boolean flaggedFraud) {
}
