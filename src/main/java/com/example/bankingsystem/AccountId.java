package com.example.bankingsystem;

import java.util.Objects;

public final class AccountId {
    private final String value;

    public AccountId(String value) {
        if (value == null || !value.matches("[0-9]{6}"))
            throw new IllegalArgumentException("Account number must contain six digits.");
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof AccountId && value.equals(((AccountId) other).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
