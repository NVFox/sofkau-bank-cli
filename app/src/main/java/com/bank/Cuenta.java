package com.bank;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Cuenta {
    private UUID numero;
    private Cliente propietario;
    private BigDecimal saldo;

    private Cuenta(Cliente propietario, BigDecimal saldo) {
        this.numero = UUID.randomUUID();
        this.propietario = propietario;
        this.saldo = saldo;
    }

    public static class FondosInsuficientes extends RuntimeException {
        public FondosInsuficientes() {
            super("No posee fondos suficientes.");
        }
    }

    public static class MontoNegativo extends RuntimeException {
        public MontoNegativo() {
            super("El monto introducido no puede ser negativo.");
        }
    }

    public static Cuenta abrirCon(Cliente cliente) {
        return new Cuenta(cliente, BigDecimal.ZERO);
    }

    public static Cuenta abrirCon(Cliente cliente, BigDecimal saldo) {
        if (saldo.compareTo(BigDecimal.ZERO) < 0)
            throw new MontoNegativo();

        return new Cuenta(cliente, saldo);
    }

    public UUID obtenerNumero() {
        return this.numero;
    }

    public BigDecimal obtenerSaldo() {
        return this.saldo;
    }

    public Cliente obtenerPropietario() {
        return this.propietario;
    }

    public void depositarFondos(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) < 0)
            throw new MontoNegativo();

        this.saldo = saldo.add(monto);
    }

    public Transaccion retirarFondos(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) < 0)
            throw new MontoNegativo();

        if (saldo.compareTo(monto) < 0)
            throw new FondosInsuficientes();

        Transaccion transaccion = Transaccion.en(this)
                .porRetiro(monto);

        this.saldo = saldo.subtract(monto);

        return transaccion;
    }

    public List<Transaccion> transferirFondos(BigDecimal monto, Cuenta a) {
        if (monto.compareTo(BigDecimal.ZERO) < 0)
            throw new MontoNegativo();

        if (saldo.compareTo(monto) < 0)
            throw new FondosInsuficientes();

        Transaccion transaccionPorEnvio = Transaccion.en(this)
                .porTransferenciaEnviada(monto);

        Transaccion transaccionPorRecibo = Transaccion.en(a)
                .porTransferenciaRecibida(monto);

        this.saldo = saldo.subtract(monto);
        a.saldo = a.saldo.add(monto);

        return List.of(transaccionPorEnvio, transaccionPorRecibo);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((numero == null) ? 0 : numero.hashCode());
        result = prime * result + ((propietario == null) ? 0 : propietario.hashCode());
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
        Cuenta other = (Cuenta) obj;
        if (numero == null) {
            if (other.numero != null)
                return false;
        } else if (!numero.equals(other.numero))
            return false;
        if (propietario == null) {
            if (other.propietario != null)
                return false;
        } else if (!propietario.equals(other.propietario))
            return false;
        return true;
    }

}
