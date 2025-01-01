package com.bank.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

import com.bank.Cliente;
import com.bank.Cuenta;

public class Cuentas {
    private Map<UUID, Cuenta> cuentas;
    private Map<Cliente, List<Cuenta>> cuentasPorCliente;

    public Cuentas() {
        this.cuentas = new HashMap<>();
        this.cuentasPorCliente = new HashMap<>();
    }

    public static class CuentaNoExiste extends RuntimeException {
        public CuentaNoExiste() {
            super("Cuenta no existe.");
        }
    }

    public Cuenta obtenerCuentaPorNumero(UUID numero) {
        return Optional.ofNullable(cuentas.getOrDefault(numero, null))
                .orElseThrow(CuentaNoExiste::new);
    }

    public List<Cuenta> obtenerCuentasPorCliente(Cliente cliente) {
        return cuentasPorCliente.getOrDefault(cliente, new LinkedList<>());
    }

    public Cuenta crearCuenta(Cliente cliente) {
        return crearCuenta(cliente, BigDecimal.ZERO);
    }

    public Cuenta crearCuenta(Cliente cliente, BigDecimal saldoInicial) {
        Cuenta cuenta = Cuenta.abrirCon(cliente, saldoInicial);

        cuentasPorCliente
                .computeIfAbsent(cliente, k -> new LinkedList<>()).add(cuenta);

        cuentas.put(cuenta.obtenerNumero(), cuenta);

        return cuenta;
    }
}
