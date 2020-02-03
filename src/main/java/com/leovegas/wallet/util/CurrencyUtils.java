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
                .currencyCode(left.getCurrencyCode())
                .build();
    }


    public static Money toMoney(String value, String currencyCode) throws IllegalArgumentException {
        BigDecimal decimal = new BigDecimal(value);
        Currency currency = Currency.getInstance(currencyCode);
        if (decimal.scale() != currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException(
                    String.format("Wrong number of fraction digits for currency : %s  != %s for: %s",
                            decimal.scale(),
                            currency.getDefaultFractionDigits(),
                            value
                    ));
        }
        return Money.builder()
                .amount(decimal)
                .currencyCode(currency.getCurrencyCode())
                .build();
    }

    private static void checkSameCurrency(Money left, Money right) {
        if (!left.getCurrencyCode().equals(right.getCurrencyCode())) {
            throw new CurrencyMismatchException(
                    String.format(
                            "%s doesn't match the expected currency: %s", left.getCurrencyCode(), right.getCurrencyCode()));
        }
    }

}
