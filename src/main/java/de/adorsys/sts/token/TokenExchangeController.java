package de.adorsys.sts.token;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.adorsys.jjwk.serverkey.KeyAndJwk;
import org.adorsys.jjwk.serverkey.KeyConverter;
import org.adorsys.jjwk.serverkey.ServerKeyManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

import de.adorsys.sts.config.TokenResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
@Api(value = "/token/token-exchange", tags = {"Token Exchange"}, description = "Token exchange, token degradation endpoint")
@TokenResource
@RequestMapping(path = "/token/token-exchange")
public class TokenExchangeController {

    @Autowired
    private ServerKeyManager keyManager;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private HttpServletRequest servletRequest;

	@Autowired
	private ResourceServerProcessor resourceServerProcessor = new ResourceServerProcessor();

	@GetMapping(consumes={MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
	@ApiOperation(value = "Exchange Token", notes = "Create an access or refresh token given a valide subject token.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = TokenResponse.class),
			@ApiResponse(code = 400, message = "Bad request", responseHeaders = @ResponseHeader(name = "error", description = "invalid request")) })
	public ResponseEntity<Object> tokenExchange(
			@ApiParam(name="grant_type", value="Indicates that a token exchange is being performed.", 
		required=true, allowMultiple=false, example="urn:ietf:params:oauth:grant-type:token-exchange", defaultValue="urn:ietf:params:oauth:grant-type:token-exchange") @RequestParam("grant_type") String grant_type, 

			@ApiParam(name="resource", value="Indicates the physical location of the target service or resource where the client intends to use the requested security token.  This enables the authorization server to apply policy as appropriate for the target, such as determining the type and content of the token to be issued or if and how the token is to be encrypted.", 
		required=false, allowMultiple=true, example="http://localhost:8080/multibanking-service") @RequestParam(name="resource", required=false) String[] resources, 

			@ApiParam(name="audience", value="The logical name of the target service where the client intends to use the requested security token.  This serves a purpose similar to the resource parameter, but with the client providing a logical name rather than a physical location.", 
		required=false, allowMultiple=true, example="http://localhost:8080/multibanking-service") @RequestParam(name="audience", required=false) String[] audiences, 

			@ApiParam(name="scope", value="A list of space-delimited, case-sensitive strings that allow the client to specify the desired scope of the requested security token in the context of the service or resource where the token will be used.", 
		required=false, allowMultiple=false, example="user banking") @RequestParam(name="scope", required=false) String scope, 

			@ApiParam(name="requested_token_type", value="An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml.", 
		required=false, allowMultiple=false, example="urn:ietf:params:oauth:token-type:jwt", defaultValue="urn:ietf:params:oauth:token-type:jwt") @RequestParam(name="requested_token_type", required=false) String requested_token_type, 

			@ApiParam(name="subject_token", value="A security token that represents the identity of the party on behalf of whom the request is being made.  Typically, the subject of this token will be the subject of the security token issued in response to this request.", 
		required=true, allowMultiple=false, example="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ") @RequestParam("subject_token") String subject_token, 

			@ApiParam(name="subject_token_type", value="An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.", 
		required=true, allowMultiple=false, example="urn:ietf:params:oauth:token-type:jwt", defaultValue="urn:ietf:params:oauth:token-type:jwt") @RequestParam("subject_token_type") String subject_token_type, 

			@ApiParam(name="actor_token", value="A security token that represents the identity of the acting party.  Typically this will be the party that is authorized to use the requested security token and act on behalf of the subject.", 
		required=false, allowMultiple=false, example="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ") @RequestParam(name="actor_token", required=false) String actor_token, 

			@ApiParam(name="actor_token_type", value="An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.", 
		required=true, allowMultiple=false, example="urn:ietf:params:oauth:token-type:jwt") @RequestParam(name="actor_token_type", required=false) String actor_token_type) 
	{
		// Validate input parameters.
		if(!StringUtils.equals("urn:ietf:params:oauth:grant-type:token-exchange", grant_type)){
			return ResponseUtils.invalidParam("Request parameter grant_type is missing or does not carry the value urn:ietf:params:oauth:grant-type:token-exchange. See https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08#section-2.1");
		}

		if(StringUtils.isBlank(subject_token)){
			return ResponseUtils.missingParam(subject_token);
		}

		if(StringUtils.isBlank(subject_token_type)){
			return ResponseUtils.missingParam(subject_token_type);
		}
		
		// If requested token type is not null, then the value must be urn:ietf:params:oauth:token-type:jwt
		if(StringUtils.isNotBlank(requested_token_type) && !StringUtils.equals("urn:ietf:params:oauth:token-type:jwt", requested_token_type)){
			return ResponseUtils.invalidParam("Request parameter requested_token_type must be left blank or carry the value urn:ietf:params:oauth:token-type:jwt. Only JWT token types are supported by this version");
		}
		
		if(!StringUtils.equals("urn:ietf:params:oauth:token-type:jwt", subject_token_type)){
			return ResponseUtils.invalidParam("Request parameter subject_token_type is missing or does not carry the value urn:ietf:params:oauth:token-type:jwt. Only JWT token types can be consumed by this version");
		}
		
		JWTClaimsSet subjectTokenClaim;
		try {
			subjectTokenClaim = validateToken(subject_token, "subject_token");
		} catch (TokenValidationException e) {
			return ResponseEntity.badRequest().body(e.getErrorData());
		}
		
		JWTClaimsSet actorTokenClaim = null;
		if(StringUtils.isNotBlank(actor_token)){
			// If actor token is not null, then the value of the actor_token_type must be urn:ietf:params:oauth:token-type:jwt
			if(!StringUtils.equals("urn:ietf:params:oauth:token-type:jwt", actor_token_type)){
				return ResponseUtils.invalidParam("The conditional parameter actor_token_type must be set when actor_token is sent and carry the value urn:ietf:params:oauth:token-type:jwt. Only JWT token types are supported by this version");
			}
			try {
				actorTokenClaim = validateToken(actor_token, "actor_token");
			} catch (TokenValidationException e) {
				return ResponseEntity.badRequest().body(e.getErrorData());
			}
		}
		
		Builder claimSetBuilder = new JWTClaimsSet.Builder();
		claimSetBuilder = claimSetBuilder.subject(subjectTokenClaim.getSubject())
					.expirationTime(subjectTokenClaim.getExpirationTime())
					.issuer(ResponseUtils.getIssuer(servletRequest))
					.issueTime(new Date())
					.jwtID(UUID.randomUUID().toString())
					.notBeforeTime(subjectTokenClaim.getNotBeforeTime())
					.claim("typ", "Bearer")
					.claim("acr", subjectTokenClaim.getClaim("acr"))
					.claim("role", "USER");
// preferred_username
		
		// Dealing with scope
		List<String> existingScope = tokenService.extractRoles(subjectTokenClaim);
		List<String> newScopeList = existingScope;
		if(StringUtils.isNotBlank(scope)){
			newScopeList = new ArrayList<>();
			String[] scopes = StringUtils.split(scope);
			for (String scopeStr : scopes) {
				if(existingScope.contains(scopeStr)){
					newScopeList.add(scopeStr);
				}
			}
		}
		if(!newScopeList.isEmpty()){
			claimSetBuilder.claim("scp", newScopeList);
		}
		
		// TODO produce user data service from controller
		List<ResourceServerAndSecret> processedResources = resourceServerProcessor.processResources(audiences, resources, null);
		// Resources or audiances
		claimSetBuilder = ResponseUtils.handleResources(claimSetBuilder, processedResources);
		
		for (ResourceServerAndSecret resourceServerAndSecret : processedResources) {
			if(!resourceServerAndSecret.hasEncryptedSecret()) continue;
			if(StringUtils.isNotBlank(resourceServerAndSecret.getResourceServer().getUserSecretClaimName())){
				claimSetBuilder.claim(resourceServerAndSecret.getResourceServer().getUserSecretClaimName(), resourceServerAndSecret.getEncryptedSecret());
			}
		}
		
		// Actor Token
		if(actorTokenClaim!=null){
			HashMap<String, Object> hashMap = new HashMap<>();
			hashMap.put("sub", actorTokenClaim.getSubject());
			hashMap.put("iss", actorTokenClaim.getIssuer());
			Object nestedActor = actorTokenClaim.getClaim("act");
			if(nestedActor!=null){
				hashMap.put("act", nestedActor);
			}
			claimSetBuilder = claimSetBuilder.claim("act", hashMap);
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
		String newScopeSpaceSparated = null;
		if(newScopeList!=null){
			for (String scopeStr : newScopeList) {
				if(newScopeSpaceSparated==null){
					newScopeSpaceSparated = scopeStr;
				} else {
					newScopeSpaceSparated = newScopeSpaceSparated + " " + scopeStr;
				}
			}
		}
		tokenResponse.setScope(newScopeSpaceSparated);
		
		return ResponseEntity.ok(tokenResponse);
	}

	private JWTClaimsSet validateToken(String token, String tokenName) throws TokenValidationException {
		JWTClaimsSet bearerToken = tokenService.checkBearerToken(token);
		if(bearerToken==null){
			ResponseEntity<Object> errorData = ResponseUtils.invalidParam("Token in field " + tokenName + " does not seam to be a valid token");
			throw new TokenValidationException(errorData);
		}
		return bearerToken;
	}
}
