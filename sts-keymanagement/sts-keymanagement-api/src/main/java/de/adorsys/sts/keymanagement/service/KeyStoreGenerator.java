package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;

import java.time.ZonedDateTime;

public interface KeyStoreGenerator {

    StsKeyStore generate();

    StsKeyEntry generateKeyEntryForFutureUsage(KeyUsage keyUsage, ZonedDateTime notBefore);

    StsKeyEntry generateSignatureKeyEntryForInstantUsage();

    StsKeyEntry generateSignatureKeyEntryForFutureUsage(ZonedDateTime notBefore);

    StsKeyEntry generateEncryptionKeyEntryForInstantUsage();

    StsKeyEntry generateEncryptionKeyEntryForFutureUsage(ZonedDateTime notBefore);

    StsKeyEntry generateSecretKeyEntryForInstantUsage();

    StsKeyEntry generateSecretKeyEntryForFutureUsage(ZonedDateTime notBefore);
}
