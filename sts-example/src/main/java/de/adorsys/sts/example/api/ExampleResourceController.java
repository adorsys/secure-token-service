package de.adorsys.sts.example.api;

import de.adorsys.sts.example.service.ExampleLoginAdapter;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ExampleResourceController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ExampleLoginAdapter loginAdapter;

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest login) {
        String username = login.getUsername();

        String token = loginAdapter.getExampleToken(username, login.getPassword());
        return encryptionService.encryptFor(username, token);
    }
}
