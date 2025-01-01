package com.bank.commands;

import com.bank.Cuenta;
import com.bank.requests.PeticionDeposito;
import com.bank.util.classes.Comando;

public class ComandoDeposito extends Comando<PeticionDeposito> {
    private final Cuenta cuenta;

    private ComandoDeposito(Cuenta cuenta, PeticionDeposito peticion) {
        super(peticion);
        this.cuenta = cuenta;
    }

    public static class Builder {
        private Cuenta cuenta;

        private Builder(Cuenta cuenta) {
            this.cuenta = cuenta;
        }

        public ComandoDeposito con(PeticionDeposito peticion) {
            return new ComandoDeposito(cuenta, peticion);
        }
    }

    public static Builder en(Cuenta cuenta) {
        return new Builder(cuenta);
    }

    public Cuenta destino() {
        return this.cuenta;
    }
}
