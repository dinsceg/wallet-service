package com.leovegas.wallet.util;

import com.leovegas.wallet.exception.CurrencyMismatchException;
import com.leovegas.wallet.model.request.Money;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Currency;


@UtilityClass
public class CurrencyUtils {

    public static Money addMoney(Money left, Money right) throws CurrencyMismatchException {

        checkSameCurrency(left, right);

        return Money.builder()
                .amount(left.getAmount().add(right.getAmount()))
                .currency(left.getCurrency())
                .build();
    }


    public static Money toMoney(String value, String currencyCode) throws IllegalArgumentException {
        BigDecimal decimal = new BigDecimal(value);
        Currency currency = Currency.getInstance(currencyCode);
        if (decimal.scale() != currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException("Wrong number of fraction digits for currency : "
                    + decimal.scale()
                    + " != " + currency.getDefaultFractionDigits() + " for: " + value);
        }
        return Money.builder()
                .amount(decimal)
                .currency(currency)
                .build();
    }

    private static void checkSameCurrency(Money left, Money right) {
        if (!left.getCurrency().equals(right.getCurrency())) {
            throw new CurrencyMismatchException(
                    left.getCurrency() + " doesn't match the expected currency: " + right.getCurrency());
        }
    }

}
