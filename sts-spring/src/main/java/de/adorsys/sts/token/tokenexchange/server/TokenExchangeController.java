package de.adorsys.sts.token.tokenexchange.server;

import com.nimbusds.jose.proc.BadJOSEException;
import de.adorsys.sts.ResponseUtils;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenRequestForm;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeRequest;
import de.adorsys.sts.token.tokenexchange.TokenExchangeService;
import de.adorsys.sts.token.tokenexchange.TokenValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenExchangeController {

    private final TokenExchangeService tokenExchangeService;

    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Exchange Token", description = "Create an access or refresh token given a valide subject token.", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", headers = @Header(name = "error", description = "invalid request"))
    })
    public ResponseEntity<Object> tokenExchange(@RequestBody @ModelAttribute TokenRequestForm tokenRequestForm, HttpServletRequest servletRequest) {
        if (log.isTraceEnabled()) log.trace("POST tokenExchange started...");

        TokenExchangeRequest tokenExchange = getTokenExchangeRequest(tokenRequestForm, servletRequest);

        String errorMessage = "";
        try {
            TokenResponse tokenResponse = tokenExchangeService.exchangeToken(tokenExchange);
            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidParameterException e) {
            errorMessage = e.getMessage();
            return ResponseUtils.invalidParam(e.getMessage());
        } catch (MissingParameterException e) {
            errorMessage = e.getMessage();
            return ResponseUtils.missingParam(e.getMessage());
        } catch (TokenValidationException e) {
            errorMessage = e.getMessage();
            ResponseEntity<Object> errorData = ResponseUtils.invalidParam(e.getMessage());
            return ResponseEntity.badRequest().body(errorData);
        } catch (BadJOSEException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("source", "sts").body(e.getMessage());
        } finally {
            if (log.isTraceEnabled()) log.trace("POST tokenExchange finished: {}", errorMessage);
        }
    }

    private static TokenExchangeRequest getTokenExchangeRequest(TokenRequestForm tokenRequestForm, HttpServletRequest servletRequest) {
        return TokenExchangeRequest.builder()
                .grantType(tokenRequestForm.getGrantType())
                .resources(tokenRequestForm.getResources())
                .subjectToken(tokenRequestForm.getSubjectToken())
                .subjectTokenType(tokenRequestForm.getSubjectTokenType())
                .actorToken(tokenRequestForm.getActorToken())
                .actorTokenType(tokenRequestForm.getActorTokenType())
                .issuer(ResponseUtils.getIssuer(servletRequest))
                .scope(tokenRequestForm.getScope())
                .requestedTokenType(tokenRequestForm.getRequestedTokenType())
                .audiences(tokenRequestForm.getAudiences())
                .build();
    }
}
