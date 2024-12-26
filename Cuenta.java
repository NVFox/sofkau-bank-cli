import java.math.BigDecimal;
import java.util.List;

public class Cuenta {
    private Cliente propietario;
    private BigDecimal saldo;

    private Cuenta(Cliente propietario, BigDecimal saldo) {
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
        return new Cuenta(cliente, saldo);
    }

    public BigDecimal obtenerSaldo() {
        return this.saldo;
    }

    public Transaccion depositarFondos(BigDecimal monto) {
        if (monto.min(BigDecimal.ZERO).equals(monto))
            throw new MontoNegativo();

        this.saldo = saldo.add(monto);

        return Transaccion.en(this)
                .porDeposito(monto);
    }

    public Transaccion retirarFondos(BigDecimal monto) {
        if (monto.min(BigDecimal.ZERO).equals(monto))
            throw new MontoNegativo();

        if (monto.min(saldo).equals(saldo))
            throw new FondosInsuficientes();

        this.saldo = saldo.subtract(monto);

        return Transaccion.en(this)
                .porRetiro(monto);
    }

    public List<Transaccion> transferirFondos(BigDecimal monto, Cuenta a) {
        if (monto.min(BigDecimal.ZERO).equals(monto))
            throw new MontoNegativo();

        if (monto.min(saldo).equals(saldo))
            throw new FondosInsuficientes();

        Transaccion transaccionPorEnvio = Transaccion.en(this)
                .porTransferenciaEnviada(monto);

        Transaccion transaccionPorRecibo = Transaccion.en(this)
                .porTransferenciaRecibida(monto);

        this.saldo = saldo.subtract(monto);
        a.saldo = saldo.add(monto);

        return List.of(transaccionPorEnvio, transaccionPorRecibo);
    }
}
