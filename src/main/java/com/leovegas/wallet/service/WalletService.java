package com.leovegas.wallet.service;


import com.leovegas.wallet.entity.AccountEntity;
import com.leovegas.wallet.entity.TransactionEntity;
import com.leovegas.wallet.exception.*;
import com.leovegas.wallet.model.TransactionType;
import com.leovegas.wallet.model.Type;
import com.leovegas.wallet.model.request.CreateAccountRequest;
import com.leovegas.wallet.model.request.Money;
import com.leovegas.wallet.model.request.TransactionRequest;
import com.leovegas.wallet.model.response.CreateAccountResponse;
import com.leovegas.wallet.model.response.TransactionResponse;
import com.leovegas.wallet.respository.AccountRepository;
import com.leovegas.wallet.respository.TransactionRepository;
import com.leovegas.wallet.util.CurrencyUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@Data
@Slf4j
@Transactional
@AllArgsConstructor
public class WalletService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Mono<CreateAccountResponse> createAccount(CreateAccountRequest account, String accountNumber) {

        return Mono.fromCallable(() ->
                accountRepository.save(AccountEntity.builder()
                        .accountNumber(accountNumber)
                        .active(account.getAccountStatus())
                        .balance(account.getMoney().getAmount())
                        .createdDate(new Date())
                        .currency(account.getMoney().getCurrencyCode())
                        .build()))
                .doOnSubscribe(subscription -> log.info("Creating account for the account ref = {}", accountNumber))
                .map(accountEntity -> CreateAccountResponse.builder()
                        .accountNumber(accountEntity.getAccountNumber())
                        .active(accountEntity.getActive())
                        .balance(accountEntity.getBalance())
                        .currency(accountEntity.getCurrency())
                        .build())
                .onErrorMap(throwable -> handleExceptions(Type.ACCOUNT, accountNumber, throwable));

    }

    public Mono<Money> getAccountBalance(String accountNumber) {

        return Mono.fromCallable(() -> accountRepository.findByAccountNumber(accountNumber))
                .switchIfEmpty(throwAccountNotFoundException(accountNumber))
                .doOnSubscribe(subscription -> log.info("Getting account balance for the account ref = {}", accountNumber))
                .map(accountEntity -> CurrencyUtils.toMoney(accountEntity.getBalance().toString(),
                        accountEntity.getCurrency()));
    }

    private Mono<AccountEntity> throwAccountNotFoundException(String accountNumber) {
        return Mono.error(new AccountNotFoundException(String.format("account '%s' does not exist", accountNumber)));
    }


    public Mono<TransactionResponse> transferFunds(TransactionRequest request, String transactionId) {

        return Mono.fromCallable(() -> accountRepository.findByAccountNumber(request.getAccountNumber()))
                .switchIfEmpty(throwAccountNotFoundException(request.getAccountNumber()))
                .doOnSubscribe(subscription -> log.debug("Transferring {} {} to the account = {}",
                        request.getMoney().getAmount(), request.getMoney().getCurrencyCode(),
                        request.getAccountNumber()))
                .doOnSuccess(accountEntity -> {
                    Money balanceToBe = checkingAccountStatusAndMoneyOverWithDrawn(request, accountEntity);
                    accountEntity.setBalance(balanceToBe.getAmount());
                })
                .map(accountEntity -> {
                    TransactionEntity transactionEntity = transactionRepository.save(TransactionEntity.builder()
                            .createdDate(new Date())
                            .transactionId(transactionId)
                            .transactionType(request.getTransactionType().getValue())
                            .accountNumber(request.getAccountNumber())
                            .amount(request.getMoney().getAmount())
                            .currency(request.getMoney().getCurrencyCode())
                            .build());

                    // updating new balance to the account
                    accountRepository.save(accountEntity);

                    return TransactionResponse.builder()
                            .accountNumber(transactionEntity.getAccountNumber())
                            .money(Money.builder()
                                    .amount(transactionEntity.getAmount())
                                    .currencyCode(transactionEntity.getCurrency())
                                    .build())
                            .transactionDate(transactionEntity.getCreatedDate())
                            .transactionType(TransactionType.fromString(transactionEntity.getTransactionType()))
                            .transactionId(transactionEntity.getTransactionId())
                            .build();

                })
                .onErrorMap(throwable -> handleExceptions(Type.TRANSACTION, transactionId, throwable));

    }

    private Exception handleExceptions(Type type, String reference, Throwable throwable) throws InvalidRequestException {
        if (throwable instanceof DataIntegrityViolationException) {
            throw new InvalidRequestException(String.format("the %s '%s' already exists", type, reference));
        } else if (throwable instanceof InsufficientFundsException) {
            throw (InsufficientFundsException) throwable;
        } else if (throwable instanceof AccountNotFoundException) {
            throw (AccountNotFoundException) throwable;
        } else if (throwable instanceof CurrencyMismatchException) {
            throw (CurrencyMismatchException) throwable;
        } else {
            throw new InfrastructureException(throwable.getMessage());
        }

    }

    private Money checkingAccountStatusAndMoneyOverWithDrawn(TransactionRequest request, AccountEntity account) {

        return Optional.ofNullable(account)
                .filter(AccountEntity::getActive)
                .map(accountEntity -> {
                    String amount = TransactionType.CREDIT == request.getTransactionType() ?
                            request.getMoney().getAmount().toString() :
                            request.getMoney().getAmount().negate().toString();

                    Money balanceToBe = CurrencyUtils.addMoney(
                            CurrencyUtils.toMoney(new BigDecimal(amount).setScale(2, BigDecimal.ROUND_DOWN).toString(),
                                    request.getMoney().getCurrencyCode()),
                            CurrencyUtils.toMoney(accountEntity.getBalance().toString(), accountEntity.getCurrency()));

                    if (balanceToBe.getAmount().compareTo(BigDecimal.ZERO) < 0.0) {
                        throw new InsufficientFundsException(String.format("Maximum money can be transferable is %s"
                                , accountEntity.getBalance().toString()));
                    }
                    return balanceToBe;
                })
                .orElseThrow(() -> new AccountNotFoundException(String.format("account '%s' is closed", request.getAccountNumber())));

    }


    public Flux<TransactionResponse> getTransactionsByAccountNumber(String accountNumber) throws AccountNotFoundException {

        return Mono.fromCallable(() -> accountRepository.findByAccountNumber(accountNumber))
                .switchIfEmpty(throwAccountNotFoundException(accountNumber))
                .flatMapMany(o -> Flux.fromIterable(transactionRepository.findByAccountNumber(accountNumber))
                        .switchIfEmpty(Flux.error(new TransactionNotFoundException(String.format("Transactions does not " +
                                "exist for the account %s", accountNumber))))
                        .doOnSubscribe(subscription -> log.info("Getting all transactions for the account = {}", accountNumber))
                        .map(transaction -> TransactionResponse.builder()
                                .transactionId(transaction.getTransactionId())
                                .transactionType(TransactionType.fromString(transaction.getTransactionType()))
                                .transactionDate(transaction.getCreatedDate())
                                .accountNumber(transaction.getAccountNumber())
                                .money(CurrencyUtils.toMoney(transaction.getAmount().toString(), transaction.getCurrency()))
                                .build()));
    }

    public Mono<TransactionResponse> getTransactionById(String transactionId) {

        return Mono.fromCallable(() -> transactionRepository.findByTransactionId(transactionId))
                .switchIfEmpty(Mono.error(new TransactionNotFoundException(String.format("Transactions does not exist " +
                        "for the transactionId %s", transactionId))))
                .doOnSubscribe(subscription -> log.info("Getting transaction details for the ref = {}", transactionId))
                .map(transaction -> TransactionResponse.builder()
                        .transactionId(transaction.getTransactionId())
                        .transactionType(TransactionType.fromString(transaction.getTransactionType()))
                        .transactionDate(transaction.getCreatedDate())
                        .accountNumber(transaction.getAccountNumber())
                        .money(CurrencyUtils.toMoney(transaction.getAmount().toString(),
                                transaction.getCurrency()))
                        .build());
    }

}
