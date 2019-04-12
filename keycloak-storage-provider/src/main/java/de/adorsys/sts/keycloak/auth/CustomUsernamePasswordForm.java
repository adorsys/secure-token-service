package de.adorsys.sts.keycloak.auth;

import de.adorsys.sts.keycloak.AuthenticatorUtil;
import de.adorsys.sts.keycloak.Constants;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.credential.CredentialInput;
import org.keycloak.events.Errors;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordUserCredentialModel;
import org.keycloak.representations.idm.CredentialRepresentation;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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
    public boolean validatePassword(AuthenticationFlowContext context, UserModel user, MultivaluedMap<String, String> inputData) {
        List<CredentialInput> credentials = new LinkedList<>();
        String password = inputData.getFirst(CredentialRepresentation.PASSWORD);
        // Patched
        PasswordUserCredentialModel credentialModel = UserCredentialModel.password(password);

        Optional<String> scope = AuthenticatorUtil.readScope(context);
        scope.ifPresent(s -> credentialModel.setNote(Constants.CUSTOM_SCOPE_NOTE_KEY, s));

        credentials.add(credentialModel);
        if (password != null && !password.isEmpty() && context.getSession().userCredentialManager().isValid(context.getRealm(), user, credentials)) {

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
            }

            return true;
        } else {
            context.getEvent().user(user);
            context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
            Response challengeResponse = invalidCredentials(context);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
            context.clearUser();
            return false;
        }
    }
}
