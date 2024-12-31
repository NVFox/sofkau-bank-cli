package com.bank.operations;

import com.bank.Cuenta;
import com.bank.Transaccion;
import com.bank.commands.ComandoRetiro;
import com.bank.requests.PeticionRetiro;
import com.bank.util.interfaces.OperacionBancaria;

public class Retiro implements OperacionBancaria<ComandoRetiro> {
    public void operar(ComandoRetiro comando) {
        PeticionRetiro peticion = comando.obtenerPeticion();
        Cuenta destino = comando.destino();

        Transaccion transaccion = Transaccion.en(destino)
                .porRetiro(peticion.monto());

        destino.retirarFondos(peticion.monto());
    }
}
