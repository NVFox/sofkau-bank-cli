package com.bank.operations;

import com.bank.Cuenta;
import com.bank.Transaccion;
import com.bank.commands.ComandoRetiro;
import com.bank.requests.PeticionRetiro;
import com.bank.util.interfaces.OperacionBancaria;

public class Retiro implements OperacionBancaria<ComandoRetiro> {
    public void operar(ComandoRetiro comando) {
        PeticionRetiro peticion = comando.obtenerPeticion();
        Cuenta origen = comando.origen();

        Transaccion transaccion = Transaccion.en(origen)
                .porRetiro(peticion.monto());

        origen.retirarFondos(peticion.monto());
    }
}
