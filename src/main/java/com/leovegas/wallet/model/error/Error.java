package com.leovegas.wallet.model.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

    private String errorDescription;
    private String errorCode;
    private int httpStatus;
}
