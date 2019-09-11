package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.cryptoutils.SecretKeyEntry;

import javax.security.auth.callback.CallbackHandler;

public interface SecretKeyGenerator {

    SecretKeyEntry generate(String alias, CallbackHandler secretKeyPassHandler);
}
