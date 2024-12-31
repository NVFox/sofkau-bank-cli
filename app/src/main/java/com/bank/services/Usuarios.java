package com.bank.services;

import java.util.Map;
import java.util.Optional;

import com.bank.Usuario;

public class Usuarios {
    private Map<String, Usuario> usuarios;

    public Usuarios() {
    }

    public static class UsuarioYaExiste extends RuntimeException {
        public UsuarioYaExiste() {
            super("Usuario ya existe.");
        }
    }

    public static class UsuarioNoExiste extends RuntimeException {
        public UsuarioNoExiste() {
            super("Usuario no existe.");
        }
    }

    public Usuario buscarUsuario(String nombre) {
        return Optional
                .ofNullable(usuarios.getOrDefault(nombre, null))
                .orElseThrow(UsuarioNoExiste::new);
    }

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.nombre()))
            throw new UsuarioYaExiste();

        usuarios.put(usuario.nombre(), usuario);

        return usuario;
    }
}
