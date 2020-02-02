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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("/wallet")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;


    @GetMapping("/accounts/{account-number}")
    @ApiOperation("Get the current account balance for the account")
    @ApiResponse(code = HTTP_OK, message = "success", response = Money.class)
    public Mono<Money> getAccountBalance(@PathVariable("account-number") String accountNumber) {

        return walletService.getAccountBalance(accountNumber);
    }

    @PostMapping("/accounts")
    @ApiOperation("Create account with minimum balance")
    @ApiResponse(code = HTTP_OK, message = "success", response = CreateAccountResponse.class)
    public Mono<CreateAccountResponse> createAccount(@RequestBody @NotNull @Valid CreateAccountRequest createAccountRequest) {

        return walletService.createAccount(createAccountRequest);
    }

    @PostMapping("/transactions")
    @ApiOperation("Credit or debit the money to the account")
    @ApiResponse(code = HTTP_OK, message = "success", response = TransactionResponse.class)
    public Mono<TransactionResponse> transferFunds(@RequestBody @NotNull @Valid TransactionRequest transactionRequest) {

        return walletService.transferFunds(transactionRequest);
    }

    @GetMapping("/accounts/{account-number}/transactions")
    @ApiOperation("Get the list of transactions for the account")
    @ApiResponse(code = HTTP_OK, message = "success", response = TransactionResponse.class)
    public Flux<TransactionResponse> getTransactionsByAccountNumber(@PathVariable("account-number") String accountNumber) {

        return walletService.getTransactionsByAccountNumber(accountNumber);
    }

    @GetMapping("/transactions/{transaction-id}")
    @ApiOperation("Get the list of transactions for the transactionId")
    @ApiResponse(code = HTTP_OK, message = "success", response = TransactionResponse.class)
    public Mono<TransactionResponse> getTransactionById(@PathVariable("transaction-id") String transactionId) {

        return walletService.getTransactionById(transactionId);
    }


}
