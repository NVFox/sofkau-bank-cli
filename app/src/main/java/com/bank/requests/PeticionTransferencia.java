package com.bank.requests;

import java.math.BigDecimal;

public record PeticionTransferencia(
        BigDecimal monto) {
}
