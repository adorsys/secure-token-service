package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;

import javax.security.auth.callback.CallbackHandler;
import java.security.KeyStore;
import java.util.function.Supplier;

public interface SecretKeyGenerator {

    ProvidedKey generate(String alias, Supplier<byte[]> keyPassword);
}
