package de.adorsys.sts.example.api;

import com.google.common.collect.Maps;
import de.adorsys.sts.example.service.ExampleLoginAdapter;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class ExampleResourceController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ExampleLoginAdapter loginAdapter;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid LoginRequest login) {
        Map<String, String> tokens = Maps.newHashMap();

        String token = loginAdapter.getExampleToken(login.getUsername(), login.getPassword());

        for(String audience : login.getAudiences()) {
            String encryptedToken = encryptionService.encryptFor(audience, token);
            tokens.put(audience, encryptedToken);
        }

        return tokens;
    }
}
