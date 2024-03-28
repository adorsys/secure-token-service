package de.adorsys.sts.secretserver;

import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.token.tokenexchange.server.TokenExchangeController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Token Exchange")
@TokenResource
@RequestMapping("${sts.secret-server.endpoint:/secret-server/token-exchange}")
public class SecretServerRestController extends TokenExchangeController {
}
