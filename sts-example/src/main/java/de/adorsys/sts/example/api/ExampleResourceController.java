package de.adorsys.sts.example.api;

import com.google.common.collect.ImmutableList;
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
        String secretToken = loginAdapter.getExampleToken(login.getUsername(), login.getPassword());

        ImmutableList<String> audiences = ImmutableList.<String>builder()
                .addAll(login.getAudiences())
                .add("sts")
                .build();

        return encryptionService.encryptFor(audiences, secretToken);
    }
}
