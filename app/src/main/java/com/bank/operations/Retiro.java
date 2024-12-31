package com.bank.operations;

import com.bank.Cuenta;
import com.bank.Transaccion;
import com.bank.commands.ComandoRetiro;
import com.bank.events.RetiroRealizado;
import com.bank.lib.observables.Event;
import com.bank.requests.PeticionRetiro;
import com.bank.util.interfaces.OperacionBancaria;

public class Retiro implements OperacionBancaria<ComandoRetiro> {
    private Event.Listeners eventListeners;

    public Retiro(Event.Listeners eventListeners) {
        this.eventListeners = eventListeners;
    }

    public void operar(ComandoRetiro comando) {
        PeticionRetiro peticion = comando.obtenerPeticion();
        Cuenta origen = comando.origen();

        Transaccion transaccion = Transaccion.en(origen)
                .porRetiro(peticion.monto());

        origen.retirarFondos(peticion.monto());

        eventListeners.dispatch(new RetiroRealizado(transaccion));
    }
}
