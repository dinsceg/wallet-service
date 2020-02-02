package com.leovegas.wallet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {

    CREDIT,
    DEBIT;

    @JsonCreator
    public static TransactionType fromString(String name) {
        return TransactionType.valueOf(name.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }

}
