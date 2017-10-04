package de.adorsys.sts.example.api;

import com.google.common.collect.Lists;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
class LoginRequest {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    private List<String> audiences = Lists.newArrayList();
}
