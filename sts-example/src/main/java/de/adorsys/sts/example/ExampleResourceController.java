package de.adorsys.sts.example;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleResourceController {

    @PostMapping
    public String login(String username, String password) {
        return null;
    }
}
