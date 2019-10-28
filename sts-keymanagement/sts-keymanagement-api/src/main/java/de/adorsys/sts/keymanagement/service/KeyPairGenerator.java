package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.KeyPairEntry;

import javax.security.auth.callback.CallbackHandler;

public interface KeyPairGenerator {

    KeyPairEntry generateSignatureKey(String alias, CallbackHandler keyPassHandler);

    KeyPairEntry generateEncryptionKey(String alias, CallbackHandler keyPassHandler);
}
