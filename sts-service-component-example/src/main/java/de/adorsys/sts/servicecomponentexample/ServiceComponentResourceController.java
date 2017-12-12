package de.adorsys.sts.servicecomponentexample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceComponentResourceController {

    @GetMapping("/helloworld")
    public String helloWorld() {
        return "Hello world";
    }
}
