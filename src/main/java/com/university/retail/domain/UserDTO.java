package com.university.retail.domain;

import com.university.retail.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class UserDTO {

    UUID id;

    @NotNull(message = "Username must be provided")
    String username;

    @NotNull(message = "Email must be provided")
    @Email(message = "Invalid email format")
    String email;
}
