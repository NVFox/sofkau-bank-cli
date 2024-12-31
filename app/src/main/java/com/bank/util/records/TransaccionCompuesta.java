package com.bank.util.records;

import com.bank.Transaccion;

public record TransaccionCompuesta(
        Transaccion saliente,
        Transaccion entrante) {
}
