package de.adorsys.sts.keycloak.auth;

import de.adorsys.sts.keycloak.AuthenticatorUtil;
import de.adorsys.sts.keycloak.Constants;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.credential.CredentialInput;
import org.keycloak.events.Errors;
import org.keycloak.models.RequiredActionProviderModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.CredentialRepresentation;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CustomUsernamePasswordForm extends UsernamePasswordForm {

    /**
     * Override the validate password so we transfer password validation result into the authentication flow context.
     * <p>
     * TODO: Discuss issue with keycloak development team and send a patch.
     */
    @Override
    public boolean validatePassword(AuthenticationFlowContext context, UserModel user, MultivaluedMap<String, String> inputData, boolean clearUser) {
        List<CredentialInput> credentials = new LinkedList<>();
        String password = inputData.getFirst(CredentialRepresentation.PASSWORD);
        // Patched
        UserCredentialModel credentialModel = UserCredentialModel.password(password);

        Optional<String> scope = AuthenticatorUtil.readScope(context);
        scope.ifPresent(s -> credentialModel.setNote(Constants.CUSTOM_SCOPE_NOTE_KEY, s));

        credentials.add(credentialModel);
        if (password != null && !password.isEmpty() && user.credentialManager().isValid(credentials)) {

            // copy notes into the user session
            // Hint: it might have been interresting to distinguish between the different type of notes
            // that can be returned by a user storage provider like:
            // - UserSesionNote
            // - AuthNote
            // - ClientNote
            // Hint: even roles could be transported using these notes.
            Object note = credentialModel.getNote(Constants.CUSTOM_USER_SECRET_NOTE_KEY);
            if (note != null) {
                context.getAuthenticationSession().setUserSessionNote(Constants.CUSTOM_USER_SECRET_NOTE_KEY, note.toString());

                // after update to keycloak 25, verify profile enabled by default, but doesn't work correcly with custom provider
                // ("Update Account Information" form appears after login and doesn't submit). So disable this action.
                RequiredActionProviderModel requiredActionVerifyProfile = context.getRealm().getRequiredActionProviderByAlias("VERIFY_PROFILE");
                requiredActionVerifyProfile.setEnabled(false);
            }

            return true;
        } else {
            context.getEvent().user(user);
            context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
            Response challengeResponse = challenge(context, Errors.INVALID_USER_CREDENTIALS);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
            context.clearUser();
            return false;
        }
    }
}
