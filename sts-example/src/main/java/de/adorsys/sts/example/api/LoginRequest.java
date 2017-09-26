package de.adorsys.sts.example.api;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
class LoginRequest {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;
}
