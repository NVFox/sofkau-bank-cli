package com.bank.commands;

import com.bank.Cuenta;
import com.bank.requests.PeticionTransferencia;
import com.bank.util.classes.Comando;

public class ComandoTransferencia extends Comando<PeticionTransferencia> {
    private final Cuenta origen;
    private final Cuenta destino;

    private ComandoTransferencia(Cuenta origen, Cuenta destino, PeticionTransferencia peticion) {
        super(peticion);
        this.origen = origen;
        this.destino = destino;
    }

    public static class Builder {
        private Cuenta origen;
        private Cuenta destino;

        private Builder(Cuenta origen) {
            this.origen = origen;
        }

        public Builder a(Cuenta destino) {
            this.destino = destino;
            return this;
        }

        public ComandoTransferencia con(PeticionTransferencia peticion) {
            return new ComandoTransferencia(origen, destino, peticion);
        }
    }

    public static Builder en(Cuenta origen) {
        return new Builder(origen);
    }

    public Cuenta origen() {
        return origen;
    }

    public Cuenta destino() {
        return destino;
    }
}
