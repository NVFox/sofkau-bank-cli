package com.bank.events;

import com.bank.Transaccion;
import com.bank.lib.observables.Event;

public class DepositoRealizado extends Event<Transaccion> {
    public DepositoRealizado(Transaccion transaccion) {
        super(transaccion);
    }
}
