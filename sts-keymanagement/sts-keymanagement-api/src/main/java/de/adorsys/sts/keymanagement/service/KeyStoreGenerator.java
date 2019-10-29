package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;
import de.adorsys.sts.keymanagement.model.GeneratedStsEntry;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyStore;

import java.time.ZonedDateTime;

public interface KeyStoreGenerator {

    StsKeyStore generate();

    GeneratedStsEntry<ProvidedKey> generateKeyEntryForFutureUsage(KeyUsage keyUsage, ZonedDateTime notBefore);

    GeneratedStsEntry<ProvidedKeyPair> generateSignatureKeyEntryForInstantUsage();

    GeneratedStsEntry<ProvidedKeyPair> generateSignatureKeyEntryForFutureUsage(ZonedDateTime notBefore);

    GeneratedStsEntry<ProvidedKeyPair> generateEncryptionKeyEntryForInstantUsage();

    GeneratedStsEntry<ProvidedKeyPair> generateEncryptionKeyEntryForFutureUsage(ZonedDateTime notBefore);

    GeneratedStsEntry<ProvidedKey> generateSecretKeyEntryForInstantUsage();

    GeneratedStsEntry<ProvidedKey> generateSecretKeyEntryForFutureUsage(ZonedDateTime notBefore);
}
