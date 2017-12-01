package de.adorsys.sts.token.tokenexchange;


import de.adorsys.sts.ResponseUtils;
import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@Api(value = "/token/token-exchange", tags = {"Token Exchange"}, description = "Token exchange, token degradation endpoint")
@TokenResource
@RequestMapping(path = "/token/token-exchange")
public class TokenExchangeController {

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private TokenExchangeService tokenExchangeService;

    @GetMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Exchange Token", notes = "Create an access or refresh token given a valide subject token.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok", response = TokenResponse.class),
            @ApiResponse(code = 400, message = "Bad request", responseHeaders = @ResponseHeader(name = "error", description = "invalid request"))})
    public ResponseEntity<Object> tokenExchange(
            @ApiParam(name = "grant_type", value = "Indicates that a token exchange is being performed.",
                    required = true, allowMultiple = false, example = "urn:ietf:params:oauth:grant-type:token-exchange", defaultValue = "urn:ietf:params:oauth:grant-type:token-exchange") @RequestParam("grant_type") String grant_type,

            @ApiParam(name = "resource", value = "Indicates the physical location of the target service or resource where the client intends to use the requested security token.  This enables the authorization server to apply policy as appropriate for the target, such as determining the type and content of the token to be issued or if and how the token is to be encrypted.",
                    required = false, allowMultiple = true, example = "http://localhost:8080/multibanking-service") @RequestParam(name = "resource", required = false) String[] resources,

            @ApiParam(name = "audience", value = "The logical name of the target service where the client intends to use the requested security token.  This serves a purpose similar to the resource parameter, but with the client providing a logical name rather than a physical location.",
                    required = false, allowMultiple = true, example = "http://localhost:8080/multibanking-service") @RequestParam(name = "audience", required = false) String[] audiences,

            @ApiParam(name = "scope", value = "A list of space-delimited, case-sensitive strings that allow the client to specify the desired scope of the requested security token in the context of the service or resource where the token will be used.",
                    required = false, allowMultiple = false, example = "user banking") @RequestParam(name = "scope", required = false) String scope,

            @ApiParam(name = "requested_token_type", value = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml.",
                    required = false, allowMultiple = false, example = "urn:ietf:params:oauth:token-type:jwt", defaultValue = "urn:ietf:params:oauth:token-type:jwt") @RequestParam(name = "requested_token_type", required = false) String requested_token_type,

            @ApiParam(name = "subject_token", value = "A security token that represents the identity of the party on behalf of whom the request is being made.  Typically, the subject of this token will be the subject of the security token issued in response to this request.",
                    required = true, allowMultiple = false, example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ") @RequestParam("subject_token") String subject_token,

            @ApiParam(name = "subject_token_type", value = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.",
                    required = true, allowMultiple = false, example = "urn:ietf:params:oauth:token-type:jwt", defaultValue = "urn:ietf:params:oauth:token-type:jwt") @RequestParam("subject_token_type") String subject_token_type,

            @ApiParam(name = "actor_token", value = "A security token that represents the identity of the acting party.  Typically this will be the party that is authorized to use the requested security token and act on behalf of the subject.",
                    required = false, allowMultiple = false, example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ") @RequestParam(name = "actor_token", required = false) String actor_token,

            @ApiParam(name = "actor_token_type", value = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.",
                    required = true, allowMultiple = false, example = "urn:ietf:params:oauth:token-type:jwt") @RequestParam(name = "actor_token_type", required = false) String actor_token_type) {
        TokenExchangeRequest tokenExchange = TokenExchangeRequest.builder()
                .grantType(grant_type)
                .resources(resources)
                .subjectToken(subject_token)
                .subjectTokenType(subject_token_type)
                .actorToken(actor_token)
                .actorTokenType(actor_token_type)
                .issuer(ResponseUtils.getIssuer(servletRequest))
                .scope(scope)
                .requestedTokenType(requested_token_type)
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
        }
    }
}
