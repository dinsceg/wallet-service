package com.leovegas.wallet.service;

import com.leovegas.wallet.entity.AccountEntity;
import com.leovegas.wallet.entity.TransactionEntity;
import com.leovegas.wallet.model.TransactionType;
import com.leovegas.wallet.model.request.CreateAccountRequest;
import com.leovegas.wallet.model.request.Money;
import com.leovegas.wallet.model.request.TransactionRequest;
import com.leovegas.wallet.model.response.CreateAccountResponse;
import com.leovegas.wallet.model.response.TransactionResponse;
import com.leovegas.wallet.respository.AccountRepository;
import com.leovegas.wallet.respository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;


    @Test
    public void test_create_account_expect_data() {

        String accountNumber = "account1";

        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .accountStatus(true)
                .money(getMoney("100"))
                .accountNumber(accountNumber)
                .build();

        Mockito.when(accountRepository.save(any(AccountEntity.class))).thenReturn(getAccountEntity(accountNumber, "100"));


        Mono<CreateAccountResponse> createAccountResponse = walletService.createAccount(createAccountRequest);

        StepVerifier.create(createAccountResponse)
                .expectNext(CreateAccountResponse.builder()
                        .accountNumber(accountNumber)
                        .active(true)
                        .balance(new BigDecimal("100"))
                        .currency("SEK")
                        .build())
                .expectComplete()
                .verify();
    }


    @Test
    public void test_get_account_balance_expect_data() {

        String accountNumber = "account1";

        Mockito.when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(getAccountEntity(accountNumber, "100.00"));

        Mono<Money> accountBalance = walletService.getAccountBalance(accountNumber);

        StepVerifier.create(accountBalance)
                .expectNext(getMoney("100.00"))
                .expectComplete()
                .verify();
    }

    private Money getMoney(String s) {
        return Money.builder()
                .amount(new BigDecimal(s))
                .currency(Currency.getInstance("SEK"))
                .build();
    }

    @Test
    public void test_transfer_fund_expect_data() {

        String accountNumber = "account1";

        Mockito.when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(getAccountEntity(accountNumber, "100.00"));

        Date transactionDate = new Date();

        TransactionEntity transactionEntity = getTransactionEntity(accountNumber, transactionDate);

        Mockito.when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);

        Mono<TransactionResponse> transactionResponse = walletService.transferFunds(TransactionRequest.builder()
                .transactionId("test")
                .transactionType(TransactionType.CREDIT)
                .accountNumber(accountNumber)
                .money(getMoney("100.00"))
                .build());

        StepVerifier.create(transactionResponse)
                .expectNext(getTransactionResponse(accountNumber, transactionDate, "100.00"))
                .expectComplete()
                .verify();
    }

    private TransactionResponse getTransactionResponse(String accountNumber, Date transactionDate, String s) {
        return TransactionResponse.builder()
                .accountNumber(accountNumber)
                .transactionId("test")
                .transactionDate(transactionDate)
                .transactionType(TransactionType.CREDIT)
                .money(getMoney(s))
                .build();
    }

    private TransactionEntity getTransactionEntity(String accountNumber, Date transactionDate) {
        return TransactionEntity.builder()
                .createdDate(transactionDate)
                .transactionId("test")
                .transactionType(TransactionType.CREDIT.getValue())
                .accountNumber(accountNumber)
                .amount(new BigDecimal("100.00"))
                .currency("SEK")
                .build();
    }

    @Test
    public void test_get_transactions_by_account_expect_data() {

        String accountNumber = "account1";

        Mockito.when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(getAccountEntity(accountNumber, "100.00"));

        Date transactionDate = new Date();

        TransactionEntity transactionEntity = getTransactionEntity(accountNumber, transactionDate);

        Mockito.when(transactionRepository.findByAccountNumber(accountNumber)).thenReturn(Collections.singletonList(transactionEntity));

        Flux<TransactionResponse> transactionResponse = walletService.getTransactionsByAccountNumber(accountNumber);

        StepVerifier.create(transactionResponse)
                .expectNext(getTransactionResponse(accountNumber, transactionDate, "100.00"))
                .expectComplete()
                .verify();
    }

    private AccountEntity getAccountEntity(String accountNumber, String s) {
        return AccountEntity.builder()
                .accountNumber(accountNumber)
                .balance(new BigDecimal(s))
                .active(true)
                .currency("SEK")
                .build();
    }


    @Test
    public void test_get_transactions_by_transactionId_expect_data() {

        String transRef = "account1";


        Date transactionDate = new Date();

        TransactionEntity transactionEntity = getTransactionEntity(transRef, transactionDate);

        Mockito.when(transactionRepository.findByTransactionId(transRef)).thenReturn(transactionEntity);

        Mono<TransactionResponse> transactionResponse = walletService.getTransactionById(transRef);

        StepVerifier.create(transactionResponse)
                .expectNext(getTransactionResponse(transRef, transactionDate, "100.00"))
                .expectComplete()
                .verify();
    }

}