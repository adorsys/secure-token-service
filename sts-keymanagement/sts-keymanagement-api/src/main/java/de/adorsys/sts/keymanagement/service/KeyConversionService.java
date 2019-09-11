package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.cryptoutils.ServerKeysHolder;

import java.security.KeyStore;

public interface KeyConversionService {

    ServerKeysHolder export(KeyStore keyStore);
}
