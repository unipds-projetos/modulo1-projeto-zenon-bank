package com.example.dto;

import com.example.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionDTO(
         Integer step,
         TransactionType type,
         BigDecimal amount,

         CustomerDTO origin,
         CustomerDTO recipient,

         Boolean isFraud,
         Boolean isFlaggedFraud
){
    public TransactionDTO{
        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(origin);
        Objects.requireNonNull(recipient);

        if(step <=0) throw new  IllegalArgumentException("O valor de step deve ser positivo: " + step);
        if (!(type instanceof TransactionType)) throw new IllegalArgumentException("O type deve estar listado: " + type);
        if (amount.signum() <= 0) throw new IllegalArgumentException("O amount deve ser positivo: " + amount);
    }
}