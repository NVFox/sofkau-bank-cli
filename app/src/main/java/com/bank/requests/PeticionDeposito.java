package com.bank.requests;

import java.math.BigDecimal;

public record PeticionDeposito(
        BigDecimal monto) {
}
