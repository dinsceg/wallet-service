package com.leovegas.wallet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {

    ACCOUNT,
    TRANSACTION;

    @JsonCreator
    public static Type fromString(String name) {
        return Type.valueOf(name.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }
}
