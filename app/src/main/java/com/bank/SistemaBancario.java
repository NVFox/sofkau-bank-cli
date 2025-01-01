package com.bank;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

import com.bank.Transaccion.Accion;
import com.bank.commands.ComandoDeposito;
import com.bank.commands.ComandoRetiro;
import com.bank.commands.ComandoTransferencia;
import com.bank.events.DepositoRealizado;
import com.bank.events.RetiroRealizado;
import com.bank.events.TransferenciaRealizada;
import com.bank.factories.FabricaOperaciones;
import com.bank.lib.dependencies.Container;
import com.bank.lib.observables.Event;
import com.bank.lib.subscribers.Subscriber;
import com.bank.requests.PeticionDeposito;
import com.bank.requests.PeticionRetiro;
import com.bank.requests.PeticionTransferencia;
import com.bank.services.Auth;
import com.bank.services.Cuentas;
import com.bank.services.Transacciones;
import com.bank.util.classes.Comando;
import com.bank.util.records.TransaccionCompuesta;

public class SistemaBancario {
    private static Scanner input = new Scanner(System.in);
    private static Container container = Container.get();

    private static Auth auth = container.resolve(Auth.class);
    private static Cuentas cuentas = container.resolve(Cuentas.class);
    private static Transacciones transacciones = container.resolve(Transacciones.class);

    private static FabricaOperaciones fabricaOperaciones = container.resolve(FabricaOperaciones.class);

    static {
        Event.Listeners listeners = container.resolve(Event.Listeners.class);

        Consumer<DepositoRealizado> crearTransaccionPorDeposito = (e) -> transacciones.crearTransaccion(e.getPayload());

        Consumer<RetiroRealizado> crearTransaccionPorRetiro = (e) -> transacciones.crearTransaccion(e.getPayload());

        Consumer<TransferenciaRealizada> crearTransaccionPorTransferencia = (e) -> {
            TransaccionCompuesta compuesta = e.getPayload();

            transacciones.crearTransaccion(compuesta.saliente());
            transacciones.crearTransaccion(compuesta.entrante());
        };

        listeners.add(Subscriber.on(DepositoRealizado.class, crearTransaccionPorDeposito));
        listeners.add(Subscriber.on(RetiroRealizado.class, crearTransaccionPorRetiro));
        listeners.add(Subscriber.on(TransferenciaRealizada.class, crearTransaccionPorTransferencia));
    }

    public static void main(String[] args) {
        boolean abierto = true;

        while (abierto) {
            bienvenida();
            System.out.println();

            System.out.print("Su elección: ");
            int eleccionCuenta = input.hasNextInt() ? input.nextInt() : 0;

            System.out.println();

            Usuario usuario = obtenerUsuarioDeCliente();

            System.out.println();

            try {
                Cliente cliente = eleccionCuenta == 1
                        ? auth.login(usuario)
                        : auth.signup(usuario);

                if (cargarPerfil(cliente) == 1)
                    abierto = false;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println();
            }
        }
    }

    private static void bienvenida() {
        System.out.println("""
                Bievenido a nuestro banco

                Por favor, seleccione una de las siguientes opciones para continuar:

                1. Iniciar sesión
                2. Crear una cuenta""");
    }

    private static Usuario obtenerUsuarioDeCliente() {
        System.out.print("Ingrese nombre de usuario: ");
        String nombre = input.hasNext() ? input.next() : null;
        input.nextLine();

        System.out.print("Ingrese contraseña: ");
        String contraseña = input.hasNext() ? input.next() : null;
        input.nextLine();

        return new Usuario(nombre, contraseña);
    }

    private static int cargarPerfil(Cliente cliente) {
        boolean abierto = true;
        int respuesta = 0;

        while (abierto) {
            List<Cuenta> cuentas = SistemaBancario.cuentas
                    .obtenerCuentasPorCliente(cliente);

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

                Tienes los siguientes productos:""", usuario.nombre()));

        System.out.println();

        System.out.println("** Cuentas");

        if (cuentas.isEmpty())
            System.out.println("Upss, parece que no tienes cuentas aún.");

        for (int i = 0; i < cuentas.size(); i++) {
            Cuenta cuenta = cuentas.get(i);
            System.out.println(" " + (i + 1) + ". Cuenta " + cuenta.obtenerNumero()
                    + ". (Saldo disponible: $" + cuenta.obtenerSaldo() + ").");
        }

        System.out.println();

        System.out.println("""
                Opciones disponibles:

                ** Selecciona el número de cuenta para acceder a ella **

                0. Crear cuenta
                00. Cerrar sesión
                000. Salir de la aplicación""");

        System.out.println();

        System.out.print("Su elección: ");

        String eleccionUsuario = input.hasNext() ? input.next() : "000";
        input.nextLine();

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

            System.out.println();

            int resultado = cargarCuenta(cuenta);

            if (resultado == 0 || resultado == 1)
                respuesta = resultado;
        } else {
            System.out.println();
        }

        return respuesta;
    }

    private static Cuenta crearCuenta(Cliente cliente) {
        System.out.println();

        while (true) {
            System.out.print("Digite saldo inicial (Si no tiene, ponga 0): ");
            BigDecimal saldoInicial = input.hasNextBigDecimal()
                    ? input.nextBigDecimal()
                    : BigDecimal.ZERO;

            try {
                return cuentas.crearCuenta(cliente, saldoInicial);
            } catch (Exception e) {
                System.out.println();
                System.out.println("Error: " + e.getMessage());
                System.out.println();
            }
        }
    }

    private static int cargarCuenta(Cuenta cuenta) {
        boolean abierto = true;
        int respuesta = 0;

        while (abierto) {
            List<Transaccion> transacciones = SistemaBancario.transacciones
                    .obtenerTransaccionesPorCuenta(cuenta);

            respuesta = cuenta(cuenta, transacciones);

            if (respuesta == 0 || respuesta == 1 || respuesta == 2)
                abierto = false;
        }

        return respuesta;
    }

    private static int cuenta(Cuenta cuenta, List<Transaccion> transacciones) {
        System.out.println(String.format("""
                -- Bienvenido a tu cuenta (%s).
                *  Saldo disponible: $%s

                Estas son tus últimas transacciones:""", cuenta.obtenerNumero(), cuenta.obtenerSaldo()));

        System.out.println();

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
                System.out.println("+$" + saldoActual.subtract(saldoAnterior));
            } else {
                System.out.println("-$" + saldoAnterior.subtract(saldoActual));
            }

            System.out.println("    " + transaccion.obtenerAccion()
                    .toString());
        }

        System.out.println();

        System.out.println("""
                Opciones disponibles:

                ** Operaciones **

                1. Depositar fondos
                2. Retirar fondos
                3. Transferir a otra cuenta

                0. Volver a mi perfil
                00. Cerrar sesión
                000. Salir de la aplicación""");

        System.out.println();

        System.out.print("Su elección: ");

        String eleccionUsuario = input.hasNext() ? input.next() : "000";
        input.nextLine();

        int respuesta = switch (eleccionUsuario) {
            case "0" -> 2;
            case "00" -> 0;
            case "000" -> 1;
            default -> -1;
        };

        System.out.println();

        if (respuesta == -1) {
            int seleccionada = Integer.parseInt(eleccionUsuario);

            try {
                Accion accion = switch (seleccionada) {
                    case 1 -> Accion.DEPOSITO;
                    case 2 -> Accion.RETIRO;
                    case 3 -> Accion.TRANSFERENCIA;
                    default -> null;
                };

                if (accion == null)
                    return respuesta;

                Comando<?> comando = switch (accion) {
                    case DEPOSITO -> depositarEnCuenta(cuenta);
                    case RETIRO -> retirarDeCuenta(cuenta);
                    case TRANSFERENCIA -> transferirDesdeCuenta(cuenta);
                };

                fabricaOperaciones.porAccion(accion)
                        .operar(comando);
            } catch (Exception e) {
                System.out.println();
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println();
        }

        return respuesta;
    }

    private static ComandoDeposito depositarEnCuenta(Cuenta cuenta) {
        System.out.print("Digite monto a depositar: ");
        BigDecimal monto = input.hasNextBigDecimal() ? input.nextBigDecimal() : BigDecimal.ZERO;

        return ComandoDeposito.en(cuenta)
                .con(new PeticionDeposito(monto));
    }

    private static ComandoRetiro retirarDeCuenta(Cuenta cuenta) {
        System.out.print("Digite monto a retirar: ");
        BigDecimal monto = input.hasNextBigDecimal() ? input.nextBigDecimal() : BigDecimal.ZERO;

        return ComandoRetiro.en(cuenta)
                .con(new PeticionRetiro(monto));
    }

    private static ComandoTransferencia transferirDesdeCuenta(Cuenta cuenta) {
        System.out.print("Digite monto a transferir: ");
        BigDecimal monto = input.hasNextBigDecimal() ? input.nextBigDecimal() : BigDecimal.ZERO;

        System.out.print("Ingrese número de la cuenta: ");
        String numero = input.hasNext() ? input.next() : "";
        input.nextLine();

        try {
            UUID numeroCuenta = UUID.fromString(numero);
            Cuenta destino = cuentas.obtenerCuentaPorNumero(numeroCuenta);

            return ComandoTransferencia.en(cuenta)
                    .a(destino)
                    .con(new PeticionTransferencia(monto));
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Número seleccionado no es válido.");
        }
    }
}
