package com.bank.events;

import com.bank.Transaccion;
import com.bank.lib.observables.Event;
import com.bank.util.records.TransaccionCompuesta;

public class TransferenciaRealizada extends Event<TransaccionCompuesta> {
    public TransferenciaRealizada(Transaccion saliente, Transaccion entrante) {
        super(new TransaccionCompuesta(saliente, entrante));
    }
}
