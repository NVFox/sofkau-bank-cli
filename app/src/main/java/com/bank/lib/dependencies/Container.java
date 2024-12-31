package com.bank.lib.dependencies;

import java.util.HashMap;
import java.util.Map;

import com.bank.lib.observables.Event.Listeners;

public class Container {
    private Map<Class<?>, ? super Object> dependencies;

    {
        dependencies.put(Listeners.class, new Listeners());
    }

    private Container() {
        this.dependencies = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<?> clazz) {
        return (T) dependencies.getOrDefault(clazz, null);
    }
}
