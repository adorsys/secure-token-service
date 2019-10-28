package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.sts.keymanagement.model.SecretKeyEntry;

import javax.security.auth.callback.CallbackHandler;
import java.util.function.Supplier;

public interface SecretKeyGenerator {

    ProvidedKey generate(String alias, Supplier<char[]> keyPassword);
}
