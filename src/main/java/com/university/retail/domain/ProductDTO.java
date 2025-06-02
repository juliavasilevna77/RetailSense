package com.university.retail.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class ProductDTO {

    UUID id;

    @NotNull(message = "Product name must be provided")
    String name;

    @NotNull(message = "Category must be provided")
    String category;

    @NotNull(message = "Price must be provided")
    Double price;

    Boolean stockStatus;
}
