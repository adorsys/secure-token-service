package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.ServerKeysHolder;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.model.UnmodifyableKeystore;

import java.security.KeyStore;

public interface KeyConversionService {

    ServerKeysHolder export(StsKeyStore keyStore);
}
