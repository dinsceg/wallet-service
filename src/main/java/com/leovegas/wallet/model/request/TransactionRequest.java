package com.leovegas.wallet.model.request;

import com.leovegas.wallet.model.TransactionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ApiModel("Transaction Request")
public class TransactionRequest {

    @NotNull
    @ApiParam(name = "Unique transaction id", allowableValues = "credit|debit")
    private TransactionType transactionType;

    @NotNull(message = "Account Number can not be null")
    @NotBlank(message = "Account Number can not be blank")
    @NotEmpty(message = "Account Number can not be empty")
    @ApiParam(name = "Unique account number")
    private String accountNumber;

    @NotNull
    @ApiParam(name = "Money contains money and currency")
    @Valid
    private Money money;


}
