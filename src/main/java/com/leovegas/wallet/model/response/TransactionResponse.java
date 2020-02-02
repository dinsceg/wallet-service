package com.leovegas.wallet.model.response;

import com.leovegas.wallet.model.TransactionType;
import com.leovegas.wallet.model.request.Money;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@ApiModel("Transaction Response")
public class TransactionResponse {

    @NotNull
    @NotBlank
    @ApiParam(name = "Unique transaction id")
    private String transactionId;

    @NotNull
    @ApiParam(name = "Unique transaction id", allowableValues = "credit|debit")
    private TransactionType transactionType;

    @NotNull
    @ApiParam(name = "Transaction date")
    private Date transactionDate;

    @NotNull
    @NotBlank
    @ApiParam(name = "Unique account number")
    private String accountNumber;

    @NotNull
    @ApiParam(name = "Money contains money and currency")
    private Money money;


}
