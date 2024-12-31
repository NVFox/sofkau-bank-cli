package com.bank.services;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bank.Cuenta;
import com.bank.Transaccion;

public class Transacciones {
    private Map<Cuenta, List<Transaccion>> transacciones;

    public List<Transaccion> obtenerTransaccionesPorCuenta(Cuenta cuenta) {
        return transacciones.getOrDefault(cuenta, new LinkedList<>());
    }

    public void crearTransaccion(Cuenta cuenta, Transaccion transaccion) {
        transacciones.computeIfAbsent(cuenta, k -> new LinkedList<>())
                .add(transaccion);
    }
}
