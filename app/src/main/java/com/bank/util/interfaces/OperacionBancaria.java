package com.bank.util.interfaces;

import com.bank.util.classes.Comando;

public interface OperacionBancaria<T extends Comando<?>> {
    void operar(T comando);
}
