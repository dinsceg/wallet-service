package com.leovegas.wallet.resources;

import com.leovegas.wallet.exception.*;
import com.leovegas.wallet.model.error.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WalletExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({InfrastructureException.class})
    public ResponseEntity<Error> handleInfrastructureException(InfrastructureException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorDescription(ex.getMessage()).build());
    }

    @ExceptionHandler({AccountNotFoundException.class, TransactionNotFoundException.class})
    public ResponseEntity<Error> handleNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Error.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .errorDescription(ex.getMessage()).build());
    }

    @ExceptionHandler({IllegalArgumentException.class, InvalidRequestException.class,
            InsufficientFundsException.class,CurrencyMismatchException.class})
    public ResponseEntity<Error> handleInvalidRequestException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .errorDescription(ex.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> unHandleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorDescription(ex.getMessage()).build());
    }
}