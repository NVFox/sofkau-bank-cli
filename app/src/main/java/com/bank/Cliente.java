package com.bank;

import java.math.BigDecimal;

public class Cliente {
    private Usuario usuario;

    private Cliente(Usuario usuario) {
        this.usuario = usuario;
    }

    public static Cliente aPartirDe(Usuario usuario) {
        return new Cliente(usuario);
    }

    public Cuenta crearCuenta() {
        return Cuenta.abrirCon(this);
    }

    public Cuenta crearCuentaCon(BigDecimal saldoInicial) {
        return Cuenta.abrirCon(this, saldoInicial);
    }

    public Usuario obtenerUsuario() {
        return usuario;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cliente other = (Cliente) obj;
        if (usuario == null) {
            if (other.usuario != null)
                return false;
        } else if (!usuario.equals(other.usuario))
            return false;
        return true;
    }

}
