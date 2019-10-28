package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;

import java.time.ZonedDateTime;

public interface KeyStoreGenerator {

    StsKeyStore generate();

    StsKeyEntry<ProvidedKey> generateKeyEntryForFutureUsage(KeyUsage keyUsage, ZonedDateTime notBefore);

    StsKeyEntry<ProvidedKeyPair> generateSignatureKeyEntryForInstantUsage();

    StsKeyEntry<ProvidedKeyPair> generateSignatureKeyEntryForFutureUsage(ZonedDateTime notBefore);

    StsKeyEntry<ProvidedKeyPair> generateEncryptionKeyEntryForInstantUsage();

    StsKeyEntry<ProvidedKeyPair> generateEncryptionKeyEntryForFutureUsage(ZonedDateTime notBefore);

    StsKeyEntry<ProvidedKey> generateSecretKeyEntryForInstantUsage();

    StsKeyEntry<ProvidedKey> generateSecretKeyEntryForFutureUsage(ZonedDateTime notBefore);
}
