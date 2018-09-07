package de.adorsys.sts.token.tokenexchange.server;

import de.adorsys.sts.common.config.TokenResource;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(
        value = TokenExchangeRestController.DEFAULT_PATH,
        tags = {"Token Exchange"},
        description = "Token exchange, token degradation endpoint"
)
@TokenResource
@RequestMapping(path = TokenExchangeRestController.DEFAULT_PATH)
public class TokenExchangeRestController extends TokenExchangeController {

    public static final String DEFAULT_PATH = "/token/token-exchange";
}
