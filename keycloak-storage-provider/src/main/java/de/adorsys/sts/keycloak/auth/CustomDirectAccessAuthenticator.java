package de.adorsys.sts.keycloak.auth;

import de.adorsys.sts.keycloak.AuthenticatorUtil;
import de.adorsys.sts.keycloak.Constants;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.directgrant.ValidatePassword;
import org.keycloak.models.UserCredentialModel;

import java.util.Optional;

public class CustomDirectAccessAuthenticator extends ValidatePassword {
    private static final String PROVIDER_ID = "custom-direct-access-authenticator";

    @Override
    public String getDisplayType() {
        return "Custom Validate Password";
    }

    @Override
    public String getId() {
        return CustomDirectAccessAuthenticator.PROVIDER_ID;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String password = this.retrievePassword(context);
        UserCredentialModel credentialModel = UserCredentialModel.password(password);

        Optional<String> scope = AuthenticatorUtil.readScope(context);
        scope.ifPresent(s -> credentialModel.setNote(Constants.CUSTOM_SCOPE_NOTE_KEY, s));

        boolean valid = context.getUser().credentialManager().isValid(credentialModel);

        if (!valid) {
            context.getEvent().user(context.getUser());
            context.getEvent().error("invalid_user_credentials");
            Response challengeResponse = this.errorResponse(Response.Status.UNAUTHORIZED.getStatusCode(), "invalid_grant",
                    "Invalid user credentials");
            context.failure(AuthenticationFlowError.INVALID_USER, challengeResponse);
        } else {
            Object note = credentialModel.getNote(Constants.CUSTOM_USER_SECRET_NOTE_KEY);
            if (note != null) {
                context.getAuthenticationSession().setUserSessionNote(Constants.CUSTOM_USER_SECRET_NOTE_KEY, note.toString());
            }
            context.success();
        }
    }
}
