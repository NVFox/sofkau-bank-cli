package com.bank.operations;

import com.bank.Cuenta;
import com.bank.Transaccion;
import com.bank.commands.ComandoTransferencia;
import com.bank.requests.PeticionTransferencia;
import com.bank.util.interfaces.OperacionBancaria;

public class Transferencia implements OperacionBancaria<ComandoTransferencia> {
    public void operar(ComandoTransferencia comando) {
        PeticionTransferencia peticion = comando.obtenerPeticion();

        Cuenta origen = comando.origen();
        Cuenta destino = comando.destino();

        Transaccion transaccionPorEnvio = Transaccion.en(origen)
                .porTransferenciaEnviada(peticion.monto());

        Transaccion transaccionPorRecibo = Transaccion.en(destino)
                .porTransferenciaRecibida(peticion.monto());

        origen.transferirFondos(peticion.monto(), destino);
    }
}
