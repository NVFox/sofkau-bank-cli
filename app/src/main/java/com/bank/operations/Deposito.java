package com.bank.operations;

import com.bank.Cuenta;
import com.bank.Transaccion;
import com.bank.commands.ComandoDeposito;
import com.bank.events.DepositoRealizado;
import com.bank.lib.observables.Event;
import com.bank.requests.PeticionDeposito;
import com.bank.util.interfaces.OperacionBancaria;

public class Deposito implements OperacionBancaria<ComandoDeposito> {
    private Event.Listeners eventListeners;

    public Deposito(Event.Listeners eventListeners) {
        this.eventListeners = eventListeners;
    }

    public void operar(ComandoDeposito comando) {
        PeticionDeposito peticion = comando.obtenerPeticion();
        Cuenta destino = comando.destino();

        Transaccion transaccion = Transaccion.en(destino)
                .porDeposito(peticion.monto());

        destino.depositarFondos(peticion.monto());

        eventListeners.dispatch(new DepositoRealizado(transaccion));
    }
}
