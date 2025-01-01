package com.bank.events;

import com.bank.Transaccion;
import com.bank.lib.observables.Event;

public class RetiroRealizado extends Event<Transaccion> {
    public RetiroRealizado(Transaccion transaccion) {
        super(transaccion);
    }
}
