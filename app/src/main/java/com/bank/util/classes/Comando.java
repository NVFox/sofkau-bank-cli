package com.bank.util.classes;

public class Comando<T> {
    private final T peticion;

    public Comando(T peticion) {
        this.peticion = peticion;
    }

    public T obtenerPeticion() {
        return peticion;
    }
}
