import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

public class SistemaBancario {
    private static Scanner input = new Scanner(System.in);

    private static Map<String, Cliente> clientes = new HashMap<>();
    private static Map<Cliente, List<Cuenta>> cuentas = new HashMap<>();

    private enum TipoValidacion {
        INICIO, CREACION;

        public static TipoValidacion porCodigo(int codigo) {
            return switch (codigo) {
                case 1 -> INICIO;
                case 2 -> CREACION;
                default -> null;
            };
        }

        public static boolean esValido(int codigo) {
            return porCodigo(codigo) != null;
        }
    }

    public static void main(String[] args) {
        boolean abierto = true;

        while (abierto) {
            renderizar(SistemaBancario::bienvenida);

            int eleccionCuenta = renderizar(SistemaBancario::eleccionUsuario);

            if (!TipoValidacion.esValido(eleccionCuenta))
                abierto = false;

            TipoValidacion tipoValidacion = TipoValidacion.porCodigo(eleccionCuenta);

            Cliente cliente = renderizar(SistemaBancario::creacionInicioCliente);

            boolean clienteEsValido = switch (tipoValidacion) {
                case INICIO -> comprobarInicioDeSesion(cliente);
                case CREACION -> comprobarCreacionDeCliente(cliente);
            };

            if (!clienteEsValido)
                continue;

            if (cargarPerfil(cliente) == 1)
                abierto = false;
        }
    }

    private static void bienvenida() {
        System.out.println("""
                Bievenido a nuestro banco

                Por favor, seleccione una de las siguientes opciones para continuar:

                1. Iniciar sesión
                2. Crear una cuenta""");
    }

    private static int eleccionUsuario() {
        System.out.print("Su elección: ");
        return input.hasNextInt() ? input.nextInt() : 0;
    }

    private static Cliente creacionInicioCliente() {
        System.out.print("Ingrese nombre de usuario: ");
        String nombre = input.hasNextLine() ? input.nextLine() : null;

        System.out.print("Ingrese contraseña: ");
        String contraseña = input.hasNextLine() ? input.nextLine() : null;

        return Cliente.aPartirDe(new Usuario(nombre, contraseña));
    }

    private static boolean comprobarInicioDeSesion(Cliente cliente) {
        Usuario usuario = cliente.obtenerUsuario();

        if (!clientes.containsKey(usuario.nombre())) {
            System.out.println("El usuario no existe.");
            return false;
        }

        Usuario almacenado = clientes.get(usuario.nombre())
                .obtenerUsuario();

        if (!almacenado.contraseña().equals(usuario.contraseña())) {
            System.out.println("La clave es incorrecta.");
            return false;
        }

        return true;
    }

    private static boolean comprobarCreacionDeCliente(Cliente cliente) {
        Usuario usuario = cliente.obtenerUsuario();

        if (clientes.containsKey(usuario.nombre())) {
            System.out.println("El usuario ya existe.");
            return false;
        }

        clientes.put(usuario.nombre(), cliente);

        return true;
    }

    private static int cargarPerfil(Cliente cliente) {
        boolean abierto = true;
        int respuesta = 0;

        while (abierto) {
            List<Cuenta> cuentas = SistemaBancario.cuentas
                    .getOrDefault(cliente, new ArrayList<>());

            respuesta = perfil(cliente, cuentas);

            if (respuesta == 0 || respuesta == 1)
                abierto = false;
        }

        return respuesta;
    }

    private static int perfil(Cliente cliente, List<Cuenta> cuentas) {
        Usuario usuario = cliente.obtenerUsuario();

        System.out.println(String.format("""
                Bienvenido de vuelta, %s

                Tienes los siguientes productos: """, usuario.nombre()));

        renderizar(() -> {
            System.out.println("** Cuentas");

            if (cuentas.isEmpty())
                System.out.println("Upss, parece que no tienes cuentas aún.");

            for (int i = 0; i < cuentas.size(); i++) {
                System.out.println(" " + (i + 1) + ". Cuenta Nro. " + (i + 1)
                        + ". (Saldo disponible: $" + cuentas.get(i).obtenerSaldo() + ").");
            }
        });

        System.out.println("""
                Opciones disponibles:

                ** Selecciona el número de cuenta para acceder a ella **

                0. Crear cuenta
                00. Cerrar sesión
                000. Salir de la aplicación""");

        String eleccionUsuario = renderizar(() -> {
            System.out.print("Su elección: ");
            return input.hasNextLine() ? input.nextLine() : "000";
        });

        return switch (eleccionUsuario) {
            case "00" -> 0;
            case "000" -> 1;
            default -> -1;
        };
    }

    private static void renderizar(Runnable vista) {
        System.out.println("\n+----\n");
        vista.run();
        System.out.println("\n+----\n");
    }

    private static <T> T renderizar(Supplier<T> vista) {
        System.out.println("\n+----\n");
        T resultado = vista.get();
        System.out.println("\n+----\n");

        return resultado;
    }

}
