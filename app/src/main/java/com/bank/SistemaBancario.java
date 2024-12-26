package com.bank;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
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
    private static Map<Cuenta, List<Transaccion>> transacciones = new HashMap<>();

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

            respuesta = renderizar(() -> perfil(cliente, cuentas));

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

        int respuesta = switch (eleccionUsuario) {
            case "00" -> 0;
            case "000" -> 1;
            default -> -1;
        };

        if (respuesta == -1) {
            int seleccionada = Integer.parseInt(eleccionUsuario);
            Cuenta cuenta = seleccionada == 0
                    ? crearCuenta(cliente)
                    : cuentas.get(seleccionada - 1);

            int resultado = cargarCuenta(cuenta);

            if (resultado == 0)
                respuesta = resultado;
        }

        return respuesta;
    }

    private static Cuenta crearCuenta(Cliente cliente) {
        while (true) {
            System.out.print("Digite saldo inicial (Si no tiene, ponga 0): ");
            BigDecimal saldoInicial = input.hasNextBigDecimal()
                    ? input.nextBigDecimal()
                    : BigDecimal.ZERO;

            try {
                Cuenta cuenta = Cuenta.abrirCon(cliente, saldoInicial);

                cuentas.computeIfAbsent(cliente, (k) -> new ArrayList<>())
                        .add(cuenta);

                return cuenta;
            } catch (Exception e) {
                renderizar(() -> System.out.println(e.getMessage()));
            }
        }
    }

    private static int cargarCuenta(Cuenta cuenta) {
        boolean abierto = true;
        int respuesta = 0;

        while (abierto) {
            List<Transaccion> transacciones = SistemaBancario.transacciones
                    .getOrDefault(cuenta, new ArrayList<>());

            respuesta = renderizar(() -> cuenta(cuenta, transacciones));

            if (respuesta == 0 || respuesta == 1 || respuesta == 2)
                abierto = false;
        }

        return respuesta;
    }

    private static int cuenta(Cuenta cuenta, List<Transaccion> transacciones) {
        System.out.println("""
                Bienvenido a tu cuenta.

                Estas son tus últimas transacciones: """);

        renderizar(() -> {
            System.out.println("** Transacciones");

            if (transacciones.isEmpty())
                System.out.println("Upss, parece que no tienes transacciones aún.");

            for (int i = 0; i < transacciones.size(); i++) {
                Transaccion transaccion = transacciones.get(i);

                String fechaFormateada = transaccion.obtenerFecha()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                BigDecimal saldoAnterior = transaccion.obtenerSaldoAnterior();
                BigDecimal saldoActual = transaccion.obtenerSaldoActual();

                System.out.print(" " + (i + 1) + ". " + fechaFormateada
                        + ". ($" + saldoAnterior + " -> $" + saldoActual + ").  ");

                if (saldoAnterior.min(saldoActual).equals(saldoAnterior)) {
                    System.out.println("+ $" + saldoActual.subtract(saldoAnterior));
                } else {
                    System.out.println("- $" + saldoAnterior.subtract(saldoActual));
                }

                System.out.println(transaccion.obtenerAccion()
                        .toString());
            }
        });

        System.out.println("""
                Opciones disponibles:

                ** Operaciones **

                1. Depositar fondos
                2. Retirar fondos
                3. Transferir a otra cuenta

                0. Volver a mi perfil
                00. Cerrar sesión
                000. Salir de la aplicación""");

        String eleccionUsuario = renderizar(() -> {
            System.out.print("Su elección: ");
            return input.hasNextLine() ? input.nextLine() : "000";
        });

        int respuesta = switch (eleccionUsuario) {
            case "0" -> 2;
            case "00" -> 0;
            case "000" -> 1;
            default -> -1;
        };

        if (respuesta == -1) {
            int seleccionada = Integer.parseInt(eleccionUsuario);

            try {
                List<Transaccion> transaccionesPorOperacion = switch (seleccionada) {
                    case 1 -> depositarEnCuenta(cuenta);
                    case 2 -> retirarDeCuenta(cuenta);
                    case 3 -> transferirDesdeCuenta(cuenta);
                    default -> new ArrayList<>();
                };

                for (Transaccion transaccion : transaccionesPorOperacion) {
                    SistemaBancario.transacciones
                            .computeIfAbsent(transaccion.obtenerCuenta(), (k) -> new ArrayList<>())
                            .add(transaccion);
                }
            } catch (Exception e) {
                renderizar(() -> System.out.println(e.getMessage()));
            }
        }

        return respuesta;
    }

    private static List<Transaccion> depositarEnCuenta(Cuenta cuenta) {
        System.out.print("Digite monto a depositar: ");
        BigDecimal monto = input.hasNextBigDecimal() ? input.nextBigDecimal() : BigDecimal.ZERO;

        return List.of(cuenta.depositarFondos(monto));
    }

    private static List<Transaccion> retirarDeCuenta(Cuenta cuenta) {
        System.out.print("Digite monto a retirar: ");
        BigDecimal monto = input.hasNextBigDecimal() ? input.nextBigDecimal() : BigDecimal.ZERO;

        return List.of(cuenta.retirarFondos(monto));
    }

    private static List<Transaccion> transferirDesdeCuenta(Cuenta cuenta) {
        System.out.print("Digite monto a transferir: ");
        BigDecimal monto = input.hasNextBigDecimal() ? input.nextBigDecimal() : BigDecimal.ZERO;

        Cuenta cuentaDestino = renderizar(() -> {
            System.out.print("Ingrese nombre del propietario de la cuenta a transferir: ");
            String nombre = input.hasNextLine() ? input.nextLine() : "";

            System.out.print("Ingrese número de la cuenta: ");
            int numero = input.hasNextInt() ? input.nextInt() : 0;

            if (!clientes.containsKey(nombre))
                throw new RuntimeException("Cliente no existe.");

            Cliente cliente = clientes.get(nombre);

            List<Cuenta> cuentas = SistemaBancario.cuentas
                    .getOrDefault(cliente, new ArrayList<>());

            if (numero <= 0 || numero > cuentas.size())
                throw new RuntimeException("Cuenta seleccionada no existe o no es válida");

            return cuentas.get(numero - 1);
        });

        return cuenta.transferirFondos(monto, cuentaDestino);
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
