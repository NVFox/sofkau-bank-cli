package com.bank.factories;

import com.bank.Transaccion.Accion;
import com.bank.lib.dependencies.Container;
import com.bank.operations.Deposito;
import com.bank.operations.Retiro;
import com.bank.operations.Transferencia;
import com.bank.util.classes.Comando;
import com.bank.util.interfaces.OperacionBancaria;

public class FabricaOperaciones {
    private final Container container;

    public FabricaOperaciones(Container container) {
        this.container = container;
    }

    public OperacionBancaria<Comando<?>> porAccion(Accion accion) {
        return switch (accion) {
            case DEPOSITO -> container.resolve(Deposito.class);
            case RETIRO -> container.resolve(Retiro.class);
            case TRANSFERENCIA -> container.resolve(Transferencia.class);
        };
    }
}
