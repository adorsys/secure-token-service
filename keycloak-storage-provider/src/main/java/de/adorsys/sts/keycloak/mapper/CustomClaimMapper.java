package de.adorsys.sts.keycloak.mapper;

import de.adorsys.sts.keycloak.Constants;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class CustomClaimMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper {
    private static final String PROVIDER_ID = "custom-claim-mapper";
    private static final List<ProviderConfigProperty> PROVIDER_CONFIG_PROPERTIES = new ArrayList<>();

    // TODO this information should be defined as a configuration of the provider
    private static final String CLAIM_NAME = "secretClaim";

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getDisplayType() {
        return "User Attribute";
    }

    @Override
    public String getHelpText() {
        return "Map user secrets to token.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return PROVIDER_CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, AuthenticatedClientSessionModel clientSession) {
        AccessToken accessToken = super.transformAccessToken(token, mappingModel, session, userSession, clientSession);

        // Put the note into the access token
        // Hint: it might have been interesting to distinguish between the different type of notes
        // that can be returned by a user storage provider like:
        // We will have to find a way to specify which note are to be included to the access token
        String note = userSession.getNote(Constants.CUSTOM_USER_SECRET_NOTE_KEY);
        if (note != null) {
            accessToken.getOtherClaims().put(CLAIM_NAME, note);
        }

        return accessToken;
    }
}
