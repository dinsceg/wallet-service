package com.leovegas.wallet.model.request;

import com.leovegas.wallet.validation.ValidCurrencyCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;


@Data
@ApiModel("Money contains amount and currency")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Money {

    @NotNull(message = "Amount can not be null")
    @ApiParam(name = "amount")
    @PositiveOrZero(message = "Amount cannot be negative")
    private BigDecimal amount;

    @NotNull(message = "currency can not be null")
    @NotBlank(message = "currency can not be blank")
    @NotEmpty(message = "currency can not be empty")
    @ApiParam(name = "currency")
    @ValidCurrencyCode
    private String currencyCode;

}
