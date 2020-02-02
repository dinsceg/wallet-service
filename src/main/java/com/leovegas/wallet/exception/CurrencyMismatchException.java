package com.leovegas.wallet.exception;


public class CurrencyMismatchException extends IllegalArgumentException {

	public CurrencyMismatchException(String message) {
		super(message);
	}
}