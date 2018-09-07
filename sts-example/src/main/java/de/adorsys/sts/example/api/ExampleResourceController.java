package de.adorsys.sts.example.api;

import de.adorsys.sts.example.service.ExampleLoginAdapter;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class ExampleResourceController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ExampleLoginAdapter loginAdapter;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid LoginRequest login) {
        String secretToken = loginAdapter.getExampleToken(login.getUsername(), login.getPassword());

        ArrayList<String> audiences = new ArrayList<>();
        audiences.addAll(login.getAudiences());
        audiences.add("sts");

        return encryptionService.encryptFor(audiences, secretToken);
    }
}
