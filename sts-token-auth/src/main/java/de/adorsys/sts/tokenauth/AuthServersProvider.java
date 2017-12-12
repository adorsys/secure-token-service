package de.adorsys.sts.tokenauth;

import java.util.Map;

public interface AuthServersProvider {
    Map<String, AuthServer> getAll();

    AuthServer get(String issuer);
}
