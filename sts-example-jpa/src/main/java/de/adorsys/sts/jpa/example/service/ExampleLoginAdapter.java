package de.adorsys.sts.jpa.example.service;

import org.springframework.stereotype.Component;

@Component
public class ExampleLoginAdapter {

    public String getExampleToken(String username, String password) {
        return "my_secret_example_token: (" + username + ":" + password + ")";
    }
}
