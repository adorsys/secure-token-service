package de.adorsys.sts.example.api;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
class LoginRequest {

    @NotNull
    @Size(min = 1)
    private String username;

    @NotNull
    @Size(min = 1)
    private String password;

    private List<String> audiences = new ArrayList<>();
}
