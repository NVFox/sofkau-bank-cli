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
}
