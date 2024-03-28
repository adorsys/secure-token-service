package de.adorsys.sts.token.tokenexchange.server;

import de.adorsys.sts.ResponseUtils;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import de.adorsys.sts.token.tokenexchange.TokenExchangeRequest;
import de.adorsys.sts.token.tokenexchange.TokenExchangeService;
import de.adorsys.sts.token.tokenexchange.TokenValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

public class TokenExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(TokenExchangeController.class);

    @Autowired
    private TokenExchangeService tokenExchangeService;

    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Exchange Token", description = "Create an access or refresh token given a valide subject token.", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", headers = @Header(name = "error", description = "invalid request"))
    })
    public ResponseEntity<Object> tokenExchange(
            @Parameter(
                    name = "grant_type",
                    description = "Indicates that a token exchange is being performed.",
                    required = true,
                    example = TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE)
            @RequestParam(
                    value = "grant_type",
                    defaultValue = TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE
            ) String grantType,

            @Parameter(
                    name = "resource",
                    description = "Indicates the physical location of the target service or resource where the client intends to use the requested security token.  This enables the authorization server to apply policy as appropriate for the target, such as determining the type and content of the token to be issued or if and how the token is to be encrypted.",
                    example = "http://localhost:8080/multibanking-service")
            @RequestParam(name = "resource", required = false) String[] resources,

            @Parameter(
                    name = "audience",
                    description = "The logical name of the target service where the client intends to use the requested security token.  This serves a purpose similar to the resource parameter, but with the client providing a logical name rather than a physical location.",
                    example = "http://localhost:8080/multibanking-service")
            @RequestParam(name = "audience", required = false) String[] audiences,

            @Parameter(
                    name = "scope",
                    description = "A list of space-delimited, case-sensitive strings that allow the client to specify the desired scope of the requested security token in the context of the service or resource where the token will be used.",
                    example = "user banking")
            @RequestParam(name = "scope", required = false) String scope,

            @Parameter(
                    name = "requested_token_type",
                    description = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml.",
                    required = false,
                    example = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
            @RequestParam(name = "requested_token_type", required = false) String requestedTokenType,

            @Parameter(
                    name = "subject_token",
                    description = "A security token that represents the identity of the party on behalf of whom the request is being made.  Typically, the subject of this token will be the subject of the security token issued in response to this request.",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
            @RequestParam("subject_token") String subjectToken,

            @Parameter(
                    name = "subject_token_type",
                    description = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.",
                    required = true,
                    example = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
            @RequestParam(value = "subject_token_type", defaultValue = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE) String subjectTokenType,

            @Parameter(
                    name = "actor_token",
                    description = "A security token that represents the identity of the acting party.  Typically this will be the party that is authorized to use the requested security token and act on behalf of the subject.",
                    required = false,
                    example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
            @RequestParam(name = "actor_token", required = false) String actorToken,

            @Parameter(
                    name = "actor_token_type",
                    description = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.",
                    required = false,
                    example = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
            @RequestParam(name = "actor_token_type", required = false) String actorTokenType,
            HttpServletRequest servletRequest
    ) {
        if (logger.isTraceEnabled()) logger.trace("POST tokenExchange started...");

        TokenExchangeRequest tokenExchange = TokenExchangeRequest.builder()
                .grantType(grantType)
                .resources(resources)
                .subjectToken(subjectToken)
                .subjectTokenType(subjectTokenType)
                .actorToken(actorToken)
                .actorTokenType(actorTokenType)
                .issuer(ResponseUtils.getIssuer(servletRequest))
                .scope(scope)
                .requestedTokenType(requestedTokenType)
                .audiences(audiences)
                .build();

        try {
            TokenResponse tokenResponse = tokenExchangeService.exchangeToken(tokenExchange);
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
}
