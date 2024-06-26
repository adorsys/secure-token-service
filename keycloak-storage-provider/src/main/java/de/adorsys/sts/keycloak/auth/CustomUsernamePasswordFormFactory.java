package de.adorsys.sts.keycloak.auth;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordFormFactory;
import org.keycloak.models.KeycloakSession;

public class CustomUsernamePasswordFormFactory extends UsernamePasswordFormFactory {

    private static final String PROVIDER_ID = "custom-auth-username-password-form";
    private static final UsernamePasswordForm SINGLETON = new CustomUsernamePasswordForm();

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Custom Username Password Form";
    }

    @Override
    public String getHelpText() {
        return "Validates a username and password from login form.";
    }
}
