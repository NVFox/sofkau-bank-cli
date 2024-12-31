package com.bank.services;

import com.bank.Cliente;
import com.bank.Usuario;

public class Clientes {
    private Usuarios usuarios;

    public Clientes(Usuarios usuarios) {
        this.usuarios = usuarios;
    }

    public Cliente buscarCliente(String nombre) {
        return Cliente.aPartirDe(usuarios.buscarUsuario(nombre));
    }

    public Cliente crearCliente(Usuario usuario) {
        return Cliente.aPartirDe(usuarios.crearUsuario(usuario));
    }
}
