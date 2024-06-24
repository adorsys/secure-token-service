package de.adorsys.sts.token.tokenexchange.server;

import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.token.tokenexchange.TokenExchangeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Token Exchange", description = "Token exchange, token degradation endpoint")
@TokenResource
@RequestMapping(path = "/token/token-exchange")
public class TokenExchangeRestController extends TokenExchangeController {

    public TokenExchangeRestController(TokenExchangeService tokenExchangeService) {
        super(tokenExchangeService);
    }
}
