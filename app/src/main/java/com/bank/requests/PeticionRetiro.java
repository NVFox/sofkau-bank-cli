package com.bank.requests;

import java.math.BigDecimal;

public record PeticionRetiro(
        BigDecimal monto) {
}
