package com.dws.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull
    @NotEmpty
    private String accountFrom;

    @NotNull
    @NotEmpty
    private String accountTo;

    @NotNull
    @Min(value = 0, message = "Amount to transfer must be positive.")
    private BigDecimal amount;

}
