package com.leovegas.wallet.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("Create Account Response")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountResponse {

    @NotNull
    @ApiParam(name = "Unique account number")
    private String accountNumber;

    @NotNull
    @ApiParam(name = "currency")
    private String currency;

    @NotNull
    @ApiParam(name = "Current balance")
    private BigDecimal balance;

    @NotNull
    @ApiParam(name = "account status")
    private Boolean active;
}
