package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;

import java.util.function.Supplier;

public interface KeyPairGenerator {

    ProvidedKeyPair generateSignatureKey(String alias, Supplier<char[]> keyPassword);

    ProvidedKeyPair generateEncryptionKey(String alias, Supplier<char[]> keyPassword);
}
