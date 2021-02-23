package ru.trympyrym.poecraft.calculator;

public class SynchronizedMinPrice {
    private Double value = null;

    public synchronized Double getValue() {
        return value;
    }

    public synchronized void registerNewValue(Double value) {
        if (this.value == null || this.value > value) {
            this.value = value;
        }
    }
}
