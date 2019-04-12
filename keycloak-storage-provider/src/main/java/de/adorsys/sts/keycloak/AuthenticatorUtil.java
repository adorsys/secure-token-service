package de.adorsys.sts.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.UserCredentialModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthenticatorUtil {

    public static Optional<String> readScope(AuthenticationFlowContext context) {
        Object scope = context.getAuthenticationSession().getClientNote(OAuth2Constants.SCOPE);

        return Optional.ofNullable(scope)
                .map(Object::toString);
    }

    public static List<String> extractAudiences(UserCredentialModel credentialInput) {
        Object scope = credentialInput.getNote(Constants.CUSTOM_SCOPE_NOTE_KEY);

        List<String> audiences = Optional.ofNullable(scope)
                .map(Object::toString)
                .map(s -> s.split(" "))
                .map(Arrays::asList)
                .orElse(new ArrayList<>());

        return audiences.stream()
                .filter(a -> !"openid".equals(a))
                .collect(Collectors.toList());
    }
}
