package com.leovegas.wallet.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("Create Account Request")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "Account Status can not be null")
    @ApiParam(name = "Account status")
    private Boolean accountStatus;

    @NotNull
    @ApiParam(name = "Money contains money and currency")
    @Valid
    private Money money;
}
