package com.university.retail.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class TransactionDTO {

    UUID id;

    @NotNull(message = "Customer ID must be provided")
    UUID customerId;

    @NotNull(message = "Product ID must be provided")
    UUID productId;

    LocalDateTime date;

    @NotNull(message = "Payment method must be provided")
    String paymentMethod;

    @NotNull(message = "Total amount must be provided")
    Double totalAmount;
}
