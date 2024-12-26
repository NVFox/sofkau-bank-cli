import java.math.BigDecimal;

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

    public void depositarFondos(BigDecimal monto) {
        if (monto.min(BigDecimal.ZERO).equals(monto))
            throw new MontoNegativo();

        saldo.add(monto);
    }

    public void retirarFondos(BigDecimal monto) {
        if (monto.min(BigDecimal.ZERO).equals(monto))
            throw new MontoNegativo();

        if (monto.min(saldo).equals(saldo))
            throw new FondosInsuficientes();

        saldo.subtract(monto);
    }
}
