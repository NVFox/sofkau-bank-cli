package com.bank.commands;

import com.bank.Cuenta;
import com.bank.requests.PeticionRetiro;
import com.bank.util.classes.Comando;

public class ComandoRetiro extends Comando<PeticionRetiro> {
    private final Cuenta cuenta;

    private ComandoRetiro(Cuenta cuenta, PeticionRetiro peticion) {
        super(peticion);
        this.cuenta = cuenta;
    }

    public static class Builder {
        private Cuenta cuenta;

        private Builder(Cuenta cuenta) {
            this.cuenta = cuenta;
        }

        public ComandoRetiro con(PeticionRetiro peticion) {
            return new ComandoRetiro(cuenta, peticion);
        }
    }

    public static Builder en(Cuenta cuenta) {
        return new Builder(cuenta);
    }

    public Cuenta origen() {
        return cuenta;
    }
}
