package de.adorsys.sts.token.passwordgrant;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.common.token.ResourceServerAndSecret;
import de.adorsys.sts.common.token.ResourceServerProcessor;
import de.adorsys.sts.common.token.ResponseUtils;
import de.adorsys.sts.common.user.DefaultObjectMapper;
import de.adorsys.sts.common.user.UserCredentials;
import de.adorsys.sts.common.user.UserDataService;
import de.adorsys.sts.token.api.TokenResponse;
import io.swagger.annotations.*;
import org.adorsys.encobject.domain.KeyCredentials;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.service.KeystoreNotFoundException;
import org.adorsys.encobject.userdata.ObjectPersistenceAdapter;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;
import org.adorsys.jjwk.serverkey.KeyAndJwk;
import org.adorsys.jjwk.serverkey.KeyConverter;
import org.adorsys.jjwk.serverkey.ServerKeyManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "/token/password-grant", tags = {"Password Grant"}, description = "Extended oAuth2 Password Grant endpoint.")
@TokenResource
@RequestMapping(path = "/token/password-grant")
public class PasswordGrantController {

    @Autowired
    private ServerKeyManager keyManager;

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private FsPersistenceFactory persFactory;

    @Autowired
    private ResourceServerProcessor resourceServerProcessor = new ResourceServerProcessor();

    @Autowired
    private UserDataNamingPolicy namingPolicy;

    private static DefaultObjectMapper objectMapper = new DefaultObjectMapper();


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
        // Validate input parameters.
        if(!StringUtils.equals("password", grant_type)){
            return ResponseUtils.invalidParam("Request parameter grant_type is missing or does not carry the value password. See https://tools.ietf.org/html/rfc6749#section-4.3.1");
        }

        if(StringUtils.isBlank(username)){
            return ResponseUtils.missingParam(username);
        }

        if(StringUtils.isBlank(password)){
            return ResponseUtils.missingParam(password);
        }

        KeyCredentials keyCredentials = namingPolicy.newKeyCredntials(username, password);
        ObjectPersistenceAdapter objectPersistenceAdapter = new ObjectPersistenceAdapter(persFactory.getEncObjectService(), keyCredentials, objectMapper);
        // Check if we have this user in the storage. If so user the record, if not create one.
        UserDataService userDataService = new UserDataService(namingPolicy, objectPersistenceAdapter);
        if(!userDataService.hasAccount()){
            try {
                userDataService.addAccount();
            } catch (KeystoreNotFoundException e) {
                throw new IllegalStateException();
            }
        }
        // Check access
        UserCredentials loadUserCredentials = userDataService.loadUserCredentials();

        JWTClaimsSet.Builder claimSetBuilder = new JWTClaimsSet.Builder();
        claimSetBuilder = claimSetBuilder.subject(username)
                .expirationTime(DateUtils.addMinutes(new Date(), 5))
                .issuer(ResponseUtils.getIssuer(servletRequest))
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(new Date())
                .claim("typ", "Bearer")
                .claim("role", "USER");

        List<ResourceServerAndSecret> processedResources = resourceServerProcessor.processResources(audiences, resources, userDataService);
        // Resources or audiances
        claimSetBuilder = ResponseUtils.handleResources(claimSetBuilder, processedResources);

        for (ResourceServerAndSecret resourceServerAndSecret : processedResources) {
            if(!resourceServerAndSecret.hasEncryptedSecret()) continue;
            claimSetBuilder.claim(resourceServerAndSecret.getResourceServer().getUserSecretClaimName(), resourceServerAndSecret.getEncryptedSecret());
        }

        JWTClaimsSet jwtClaimsSet = claimSetBuilder.build();

        KeyAndJwk randomKey = keyManager.getKeyMap().randomSignKey();
        JWSAlgorithm jwsAlgo = KeyConverter.getJWSAlgo(randomKey);

        JWSHeader jwsHeader = new JWSHeader.Builder(jwsAlgo)
                .type(JOSEObjectType.JWT)
                .keyID(randomKey.jwk.getKeyID())
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader,jwtClaimsSet);
        try {
            signedJWT.sign(KeyConverter.findSigner(randomKey));
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess_token(signedJWT.serialize());
        tokenResponse.setIssued_token_type(TokenResponse.ISSUED_TOKEN_TYPE_ACCESS_TOKEN);
        tokenResponse.setToken_type(TokenResponse.TOKEN_TYPE_BEARER);
        int expires_in = (int) ((jwtClaimsSet.getExpirationTime().getTime() - new Date().getTime())/1000);
        tokenResponse.setExpires_in(expires_in);

        return ResponseEntity.ok(tokenResponse);
    }

}
