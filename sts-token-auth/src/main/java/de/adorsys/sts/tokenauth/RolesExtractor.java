package de.adorsys.sts.tokenauth;

import com.nimbusds.jwt.JWTClaimsSet;

import java.util.ArrayList;
import java.util.List;

public class RolesExtractor {
    private final KeycloakTokenRolesParser keycloakTokenRolesParser = new KeycloakTokenRolesParser();
    private final StringListRolesParser stringListRolesParser = new StringListRolesParser();

    public List<String> extractRoles(JWTClaimsSet claimSet) {
        List<String> results = new ArrayList<>();

        stringListRolesParser.extractRoles(claimSet, "scp", results);
        stringListRolesParser.extractRoles(claimSet, "roles", results);

        keycloakTokenRolesParser.parseRoles(claimSet, results);

        return results;
    }
}
