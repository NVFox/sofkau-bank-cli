package com.bank.lib.subscribers;

import java.util.function.Consumer;

import com.bank.lib.observables.Event;

public class Subscriber<T extends Event<?>> {
    private final Class<T> event;
    private final Consumer<T> action;

    private Subscriber(Class<T> event, Consumer<T> action) {
        this.event = event;
        this.action = action;
    }

    public static <T extends Event<?>> Subscriber<T> on(Class<T> event, Consumer<T> action) {
        return new Subscriber<>(event, action);
    }

    public Class<T> event() {
        return this.event;
    }

    public void update(T event) {
        this.action.accept(event);
    }
}
