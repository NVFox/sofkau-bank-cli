package com.bank.lib.observables;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

import com.bank.lib.subscribers.Subscriber;

public class Event<T> {
    private T payload;

    public Event(T payload) {
        this.payload = payload;
    }

    public static class Listeners {
        private Map<Class<? extends Event<?>>, List<Subscriber<? extends Event<?>>>> listeners;

        public Listeners() {
            this.listeners = new HashMap<>();
        }

        public <T> void add(Subscriber<? extends Event<?>> subscriber) {
            this.listeners.computeIfAbsent(subscriber.event(), k -> new LinkedList<>())
                    .add(subscriber);
        }

        @SuppressWarnings("unchecked")
        public <T extends Event<?>> void dispatch(T event) {
            forEvent(event.getClass())
                    .forEach(listener -> ((Subscriber<T>) listener).update(event));
        }

        private List<Subscriber<? extends Event<?>>> forEvent(Class<?> event) {
            return this.listeners.get(event);
        }
    }

    public T getPayload() {
        return this.payload;
    }
}
