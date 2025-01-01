package com.bank.services;

import com.bank.Cliente;
import com.bank.Usuario;

public class Auth {
    private Clientes clientes;

    public Auth(Clientes clientes) {
        this.clientes = clientes;
    }

    public static class CredencialesIncorrectas extends RuntimeException {
        public CredencialesIncorrectas() {
            super("Las credenciales introducidas son incorrectas.");
        }
    }

    public Cliente login(Usuario usuario) {
        Cliente cliente = clientes
                .buscarCliente(usuario.nombre());

        Usuario almacenado = cliente.obtenerUsuario();
        String contraseña = usuario.contraseña();

        if (!contraseña.equals(almacenado.contraseña()))
            throw new CredencialesIncorrectas();

        return cliente;
    }

    public Cliente signup(Usuario usuario) {
        return clientes.crearCliente(usuario);
    }
}
