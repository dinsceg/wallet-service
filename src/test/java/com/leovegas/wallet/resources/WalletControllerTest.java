package com.leovegas.wallet.resources;


import com.leovegas.wallet.model.TransactionType;
import com.leovegas.wallet.model.request.CreateAccountRequest;
import com.leovegas.wallet.model.request.Money;
import com.leovegas.wallet.model.request.TransactionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void test_create_account_expect_success() {
        String accountNumber = "account1";
        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);
    }

    private WebTestClient.RequestHeadersSpec<?> createAccount(String accountNumber) {
        return webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}")
                        .build(accountNumber))
                .bodyValue(CreateAccountRequest.builder()
                        .accountStatus(true)
                        .money(Money.builder()
                                .currencyCode("SEK")
                                .amount(new BigDecimal("100.00"))
                                .build())
                        .build());
    }

    @Test
    public void test_create_account_expect_account_already_exist() {
        String accountNumber = "account2";
        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    public void test_get_account_balance_expect_success() {
        String accountNumber = "account3";
        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}")
                        .build(accountNumber))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(100.00);

    }

    @Test
    public void test_get_account_balance_expect_account_not_found_exception() {
        String accountNumber = "account14";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}")
                        .build(accountNumber))
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void test_credit_transaction_request_expect_success() {
        String accountNumber = "account4";

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk();

        createTransaction(accountNumber, TransactionType.CREDIT, "100", "SEK")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}")
                        .build(accountNumber))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(200.00);


    }

    private WebTestClient.RequestHeadersSpec<?> createTransaction(String accountNumber, TransactionType transactionType,
                                                                  String amount, String currency) {
        return webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/wallet/transactions/{transactionId}")
                        .build(UUID.randomUUID().toString()))
                .bodyValue(TransactionRequest.builder()
                        .accountNumber(accountNumber)
                        .transactionType(transactionType)
                        .money(Money.builder()
                                .currencyCode(currency)
                                .amount(new BigDecimal(amount))
                                .build())
                        .build());
    }

    @Test
    public void test_debit_transaction_request_expect_success() {
        String accountNumber = "account5";

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk();

        createTransaction(accountNumber, TransactionType.DEBIT, "50", "SEK")

                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}")
                        .build(accountNumber))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(50.00);


    }

    @Test
    public void test_debit_transaction_request_expect_insufficientFund_exception() {
        String accountNumber = "account6";

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk();

        createTransaction(accountNumber, TransactionType.DEBIT, "101", "SEK")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorDescription").isEqualTo("Maximum money can be transferable is 100.00");

    }

    @Test
    public void test_transaction_request_expect_account_not_found_exception() {
        String accountNumber = "account7";

        createTransaction(accountNumber, TransactionType.CREDIT, "100", "SEK")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errorDescription").isEqualTo("account '" + accountNumber + "' does not exist");

    }

    @Test
    public void test_transaction_request_expect_account_inactive_exception() {
        String accountNumber = "account8";

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}")
                        .build(accountNumber))
                .bodyValue(CreateAccountRequest.builder()
                        .accountStatus(false)
                        .money(Money.builder()
                                .currencyCode("SEK")
                                .amount(new BigDecimal("100.00"))
                                .build())
                        .build())
                .exchange()
                .expectStatus().isOk();

        createTransaction(accountNumber, TransactionType.CREDIT, "100", "SEK")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errorDescription").isEqualTo("account '" + accountNumber + "' is closed");

    }

    @Test
    public void test_transaction_request_expect_currency_mismatch_exception() {
        String accountNumber = "account9";

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk();

        createTransaction(accountNumber, TransactionType.CREDIT, "100", "EUR")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorDescription").isEqualTo("EUR doesn't match the expected currency: SEK");

    }

    @Test
    public void test_get_transaction_by_account_request_expect_success() {
        String accountNumber = "account10";

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk();

        createTransaction(accountNumber, TransactionType.CREDIT, "100", "SEK")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}/transactions")
                        .build(accountNumber))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].money.amount").isEqualTo("100.0");


    }

    @Test
    public void test_get_transaction_by_account_request_expect_account_not_found_exception() {
        String accountNumber = "account11";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/accounts/{accountNumber}/transactions")
                        .build(accountNumber))
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void test_get_transaction_by_id_request_expect_success() {
        String accountNumber = "account12";
        String transactionRef = UUID.randomUUID().toString();

        createAccount(accountNumber)
                .exchange()
                .expectStatus().isOk();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/wallet/transactions/{transactionRef}")
                        .build(transactionRef))
                .bodyValue(TransactionRequest.builder()
                        .accountNumber(accountNumber)
                        .transactionType(TransactionType.CREDIT)
                        .money(Money.builder()
                                .currencyCode("SEK")
                                .amount(new BigDecimal("150"))
                                .build())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountNumber").isEqualTo(accountNumber);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/transactions/{transactionId}")
                        .build(transactionRef))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.money.amount").isEqualTo("150.0");


    }

    @Test
    public void test_get_transaction_by_id_request_expect_transaction_not_found_exception() {
        String transactionRef = UUID.randomUUID().toString();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/wallet/transactions/{transactionId}")
                        .build(transactionRef))
                .exchange()
                .expectStatus().isNotFound();

    }

}