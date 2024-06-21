package de.adorsys.sts.token.api;

import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
@AllArgsConstructor(onConstructor_ = @ConstructorProperties({"grant_type", "resource", "audience", "scope", "requested_token_type", "subject_token", "subject_token_type", "actor_token", "actor_token_type"}))
@Schema(description = "Carries request form properties of a token-exchange request",
        requiredProperties = {"grant_type", "subject_token", "subject_token_type"})
public class TokenRequestForm {

    @Schema(name = "grant_type",
            description = "Indicates that a token exchange is being performed.",
            example = TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE)
    private String grantType;

    @Schema(name = "resource",
            description = "Indicates the physical location of the target service or resource where the client intends to use the requested security token.  This enables the authorization server to apply policy as appropriate for the target, such as determining the type and content of the token to be issued or if and how the token is to be encrypted.",
            example = "http://localhost:8080/multibanking-service")
    private String[] resources;

    @Schema(name = "audience",
            description = "The logical name of the target service where the client intends to use the requested security token.  This serves a purpose similar to the resource parameter, but with the client providing a logical name rather than a physical location.",
            example = "http://localhost:8080/multibanking-service")
    private String[] audiences;

    @Schema(name = "scope",
            description = "A list of space-delimited, case-sensitive strings that allow the client to specify the desired scope of the requested security token in the context of the service or resource where the token will be used.",
            example = "user banking")
    private String scope;

    @Schema(name = "requested_token_type",
            description = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml.",
            example = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
    private String requestedTokenType;

    @Schema(name = "subject_token",
            description = "A security token that represents the identity of the party on behalf of whom the request is being made.  Typically, the subject of this token will be the subject of the security token issued in response to this request.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
    private String subjectToken;

    @Schema(name = "subject_token_type",
            description = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.",
            example = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
    private String subjectTokenType;

    @Schema(name = "actor_token",
            description = "A security token that represents the identity of the acting party.  Typically this will be the party that is authorized to use the requested security token and act on behalf of the subject.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
    private String actorToken;

    @Schema(name = "actor_token_type",
            description = "An identifier for the type of the requested security token.  If the requested type is unspecified, the issued token type is at the discretion of the authorization server and may be dictated by knowledge of the requirements of the service or resource indicated by the resource or audience parameter. This can be urn:ietf:params:oauth:token-type:jwt or urn:ietf:params:oauth:token-type:saml. This can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token.",
            example = TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
    private String actorTokenType;
}
