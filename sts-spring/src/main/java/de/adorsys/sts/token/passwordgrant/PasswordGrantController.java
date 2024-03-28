package de.adorsys.sts.token.passwordgrant;

import de.adorsys.sts.ResponseUtils;
import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Password Grant", description = "Extended oAuth2 Password Grant endpoint.")
@TokenResource
@RequestMapping(path = "/token/password-grant")
public class PasswordGrantController {

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private PasswordGrantService passwordGrantService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Password Grant", description = "Implements the oauth2 Pasword grant type. Works only if server is configured to accept password grant", responses = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", headers = @Header(name = "error", description = "invalid request"))
    })
    public ResponseEntity<Object> passwordGrant(
            @Parameter(name = "grant_type", description = "Indicates that a token exchange is being performed.", required = true, example = "password") @RequestParam("grant_type") String grant_type,
            @Parameter(name = "resource", description = "Indicates the physical location of the target service or resource where the client intends to use the requested security token.  This enables the authorization server to apply policy as appropriate for the target, such as determining the type and content of the token to be issued or if and how the token is to be encrypted.", required = false, example = "http://localhost:8080/multibanking-service") @RequestParam(name = "resource", required = false) String[] resources,
            @Parameter(name = "audience", description = "The logical name of the target service where the client intends to use the requested security token.  This serves a purpose similar to the resource parameter, but with the client providing a logical name rather than a physical location.", required = false, example = "http://localhost:8080/multibanking-service") @RequestParam(name = "audience", required = false) String[] audiences,
            @Parameter(name = "scope", description = "A list of space-delimited, case-sensitive strings that allow the client to specify the desired scope of the requested security token in the context of the service or resource where the token will be used.", required = false, example = "user banking") @RequestParam(name = "scope", required = false) String scope,
            @Parameter(name = "username", description = "The resource owner username..", required = true, example = "max.musterman") @RequestParam("username") String username,
            @Parameter(name = "password", description = "The resource owner password.", required = true, example = "SamplePassword") @RequestParam("password") String password) {
        try {
            TokenResponse tokenResponse = passwordGrantService.passwordGrant(grant_type, resources, audiences,
                    ResponseUtils.getIssuer(servletRequest), scope, username, password);

            return ResponseEntity.ok(tokenResponse);
        } catch (InvalidParameterException e) {
            return ResponseUtils.invalidParam(e.getMessage());
        } catch (MissingParameterException e) {
            return ResponseUtils.missingParam(e.getMessage());
        }
    }
}
