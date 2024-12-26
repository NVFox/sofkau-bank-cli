import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {
    private Cuenta cuenta;
    private Accion accion;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoActual;
    private LocalDateTime fecha;

    private Transaccion(Cuenta cuenta, Accion accion) {
        this.cuenta = cuenta;
        this.accion = accion;
        this.fecha = LocalDateTime.now();
    }

    public enum Accion {
        RETIRO, DEPOSITO
    }

    public static class Builder {
        private Cuenta cuenta;

        private Builder(Cuenta cuenta) {
            this.cuenta = cuenta;
        }

        public Transaccion porDeposito(BigDecimal monto) {
            Transaccion transaccion = new Transaccion(cuenta, Accion.DEPOSITO);

            transaccion.saldoAnterior = cuenta.obtenerSaldo();
            transaccion.saldoActual = cuenta.obtenerSaldo().add(monto);

            return transaccion;
        }

        public Transaccion porRetiro(BigDecimal monto) {
            Transaccion transaccion = new Transaccion(cuenta, Accion.RETIRO);

            transaccion.saldoAnterior = cuenta.obtenerSaldo();
            transaccion.saldoActual = cuenta.obtenerSaldo().subtract(monto);

            return transaccion;
        }
    }

    public static Builder en(Cuenta cuenta) {
        return new Builder(cuenta);
    }
}
