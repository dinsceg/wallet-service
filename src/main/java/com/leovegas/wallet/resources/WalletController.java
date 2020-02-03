package com.leovegas.wallet.resources;

import com.leovegas.wallet.model.request.CreateAccountRequest;
import com.leovegas.wallet.model.request.Money;
import com.leovegas.wallet.model.request.TransactionRequest;
import com.leovegas.wallet.model.response.CreateAccountResponse;
import com.leovegas.wallet.model.response.TransactionResponse;
import com.leovegas.wallet.service.WalletService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("/wallet")
@AllArgsConstructor
@Validated
public class WalletController {

    private final WalletService walletService;


    @GetMapping("/accounts/{account-number}")
    @ApiOperation("Get the current account balance for the account")
    @ApiResponse(code = HTTP_OK, message = "success", response = Money.class)
    public Mono<Money> getAccountBalance(@PathVariable("account-number") @NotNull @NotEmpty @NotBlank String accountNumber) {

        return walletService.getAccountBalance(accountNumber);
    }

    @PostMapping("/accounts/{account-number}")
    @ApiOperation("Create account with minimum balance")
    @ApiResponse(code = HTTP_OK, message = "success", response = CreateAccountResponse.class)
    public Mono<CreateAccountResponse> createAccount(@Valid @RequestBody @NotNull CreateAccountRequest createAccountRequest,
                                                     @PathVariable("account-number") @NotNull @NotEmpty @NotBlank String accountNumber) {

        return walletService.createAccount(createAccountRequest, accountNumber);
    }

    @PostMapping("/transactions/{transaction-id}")
    @ApiOperation("Credit or debit the money to the account")
    @ApiResponse(code = HTTP_OK, message = "success", response = TransactionResponse.class)
    public Mono<TransactionResponse> transferFunds(@Valid @RequestBody @NotNull TransactionRequest transactionRequest,
                                                   @PathVariable("transaction-id") @NotNull @NotEmpty @NotBlank String transactionId) {

        return walletService.transferFunds(transactionRequest, transactionId);
    }

    @GetMapping("/accounts/{account-number}/transactions")
    @ApiOperation("Get the list of transactions for the account")
    @ApiResponse(code = HTTP_OK, message = "success", response = TransactionResponse.class)
    public Flux<TransactionResponse> getTransactionsByAccountNumber(@PathVariable("account-number") @NotNull @NotEmpty @NotBlank String accountNumber) {

        return walletService.getTransactionsByAccountNumber(accountNumber);
    }

    @GetMapping("/transactions/{transaction-id}")
    @ApiOperation("Get the list of transactions for the transactionId")
    @ApiResponse(code = HTTP_OK, message = "success", response = TransactionResponse.class)
    public Mono<TransactionResponse> getTransactionById(@PathVariable("transaction-id") @NotNull @NotEmpty @NotBlank String transactionId) {

        return walletService.getTransactionById(transactionId);
    }


}
