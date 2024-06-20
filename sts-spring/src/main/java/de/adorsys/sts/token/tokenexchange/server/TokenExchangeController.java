package de.adorsys.sts.token.tokenexchange.server;

import de.adorsys.sts.ResponseUtils;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenExchangeApi;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeRequest;
import de.adorsys.sts.token.tokenexchange.TokenExchangeService;
import de.adorsys.sts.token.tokenexchange.TokenValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
public class TokenExchangeController implements TokenExchangeApi {

    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);

    private final TokenExchangeService tokenExchangeService;
    @Autowired
    private HttpServletRequest servletRequest;

    @Override
    public ResponseEntity tokenExchange(String grantType, List<String> resource, List<String> audience, String scope,
                                        String requestedTokenType, String subjectToken, String subjectTokenType,
                                        String actorToken, String actorTokenType) {
        if (logger.isTraceEnabled()) logger.trace("POST tokenExchange started...");

        TokenExchangeRequest tokenExchange = TokenExchangeRequest.builder()
                .grantType(grantType)
                .resources(nullSafeListToArray(resource))
                .subjectToken(subjectToken)
                .subjectTokenType(subjectTokenType)
                .actorToken(actorToken)
                .actorTokenType(actorTokenType)
                .issuer(ResponseUtils.getIssuer(servletRequest))
                .scope(scope)
                .requestedTokenType(requestedTokenType)
                .audiences(nullSafeListToArray(audience))
                .build();

        try {
            de.adorsys.sts.token.model.TokenResponse tokenResponse = mapTokenResponse(tokenExchangeService.exchangeToken(tokenExchange));
            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidParameterException e) {
            return ResponseUtils.invalidParam(e.getMessage());
        } catch (MissingParameterException e) {
            return ResponseUtils.missingParam(e.getMessage());
        } catch (TokenValidationException e) {
            ResponseEntity<Object> errorData = ResponseUtils.invalidParam(e.getMessage());
            return ResponseEntity.badRequest().body(errorData);
        } finally {
            if (logger.isTraceEnabled()) logger.trace("POST tokenExchange finished.");
        }
    }

    private static String[] nullSafeListToArray(List<String> list) {
        return list == null ? null : list.toArray(new String[0]);
    }

    de.adorsys.sts.token.model.TokenResponse mapTokenResponse(TokenResponse tokenResponse) {
        return new de.adorsys.sts.token.model.TokenResponse()
                .accessToken(tokenResponse.getAccess_token())
                .refreshToken(tokenResponse.getRefresh_token())
                .tokenType(tokenResponse.getToken_type())
                .issuedTokenType(tokenResponse.getIssued_token_type())
                .scope(tokenResponse.getScope())
                .expiresIn(tokenResponse.getExpires_in());
    }
}
