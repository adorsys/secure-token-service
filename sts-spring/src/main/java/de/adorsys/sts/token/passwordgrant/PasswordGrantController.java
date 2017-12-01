package de.adorsys.sts.token.passwordgrant;


import de.adorsys.sts.ResponseUtils;
import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessorService;
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
@Api(value = "/token/password-grant", tags = {"Password Grant"}, description = "Extended oAuth2 Password Grant endpoint.")
@TokenResource
@RequestMapping(path = "/token/password-grant")
public class PasswordGrantController {

    @Autowired
    private KeyManagementService keyManager;

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private ResourceServerProcessorService resourceServerProcessorService;

    @Autowired
    private PasswordGrantService passwordGrantService;

    @GetMapping(path="", consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Password Grant", notes = "Implements the oauth2 Pasword grant type. Works only if server is configured to accept password grant")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = TokenResponse.class),
            @ApiResponse(code = 400, message = "Bad request", responseHeaders = @ResponseHeader(name = "error", description = "invalid request")) })
    public ResponseEntity<Object> passwordGrant(
            @ApiParam(name="grant_type", value="Indicates that a token exchange is being performed.",
                    required=true, allowMultiple=false, example="password", defaultValue="password") @RequestParam("grant_type") String grant_type,

            @ApiParam(name="resource", value="Indicates the physical location of the target service or resource where the client intends to use the requested security token.  This enables the authorization server to apply policy as appropriate for the target, such as determining the type and content of the token to be issued or if and how the token is to be encrypted.",
                    required=false, allowMultiple=true, example="http://localhost:8080/multibanking-service") @RequestParam(name="resource", required=false) String[] resources,

            @ApiParam(name="audience", value="The logical name of the target service where the client intends to use the requested security token.  This serves a purpose similar to the resource parameter, but with the client providing a logical name rather than a physical location.",
                    required=false, allowMultiple=true, example="http://localhost:8080/multibanking-service") @RequestParam(name="audience", required=false) String[] audiences,

            @ApiParam(name="scope", value="A list of space-delimited, case-sensitive strings that allow the client to specify the desired scope of the requested security token in the context of the service or resource where the token will be used.",
                    required=false, allowMultiple=false, example="user banking") @RequestParam(name="scope", required=false) String scope,

            @ApiParam(name="username", value="The resource owner username..",
                    required=true, allowMultiple=false, example="max.musterman") @RequestParam("username") String username,

            @ApiParam(name="password", value="The resource owner password.",
                    required=true, allowMultiple=false, example="SamplePassword") @RequestParam("password") String password)
    {
        try {
            TokenResponse tokenResponse = passwordGrantService.passwordGrant(
                    grant_type,
                    resources,
                    audiences,
                    ResponseUtils.getIssuer(servletRequest),
                    scope,
                    username,
                    password
            );

            return ResponseEntity.ok(tokenResponse);
        } catch(InvalidParameterException e) {
            return ResponseUtils.invalidParam(e.getMessage());
        } catch(MissingParameterException e) {
            return ResponseUtils.missingParam(e.getMessage());
        }
    }
}
