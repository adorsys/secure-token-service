package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.cryptoutils.KeyPairEntry;

import javax.security.auth.callback.CallbackHandler;

public interface KeyPairGenerator {

    KeyPairEntry generateSignatureKey(String alias, CallbackHandler keyPassHandler);

    KeyPairEntry generateEncryptionKey(String alias, CallbackHandler keyPassHandler);
}
