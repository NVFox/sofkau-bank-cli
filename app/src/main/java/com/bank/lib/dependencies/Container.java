package com.bank.lib.dependencies;

import java.util.HashMap;
import java.util.Map;

import com.bank.factories.FabricaOperaciones;
import com.bank.lib.observables.Event.Listeners;
import com.bank.operations.Deposito;
import com.bank.operations.Retiro;
import com.bank.operations.Transferencia;
import com.bank.services.Auth;
import com.bank.services.Clientes;
import com.bank.services.Cuentas;
import com.bank.services.Transacciones;
import com.bank.services.Usuarios;

public class Container {
    private static Container instance = null;
    private Map<Class<?>, ? super Object> dependencies = new HashMap<>();

    {
        dependencies.put(Listeners.class, new Listeners());
        dependencies.put(Usuarios.class, new Usuarios());
        dependencies.put(Cuentas.class, new Cuentas());
        dependencies.put(Transacciones.class, new Transacciones());

        dependencies.put(Clientes.class, new Clientes(resolve(Usuarios.class)));
        dependencies.put(Auth.class, new Auth(resolve(Clientes.class)));

        dependencies.put(Deposito.class, new Deposito(resolve(Listeners.class)));
        dependencies.put(Retiro.class, new Retiro(resolve(Listeners.class)));
        dependencies.put(Transferencia.class, new Transferencia(resolve(Listeners.class)));

        dependencies.put(FabricaOperaciones.class, new FabricaOperaciones(this));
    }

    private Container() {
    }

    public static Container get() {
        if (instance == null)
            instance = new Container();

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<?> clazz) {
        return (T) dependencies.getOrDefault(clazz, null);
    }
}
