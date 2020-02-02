package com.leovegas.wallet.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;


@Data
@ApiModel("Money contains money and currency")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Money {

    @NotNull
    @ApiParam(name = "money")
    private BigDecimal amount;

    @NotNull
    @ApiParam(name = "currency")
    private Currency currency;

}
