package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.ServerKeysHolder;
import de.adorsys.sts.keymanagement.model.StsKeyStore;

public interface KeyConversionService {

    ServerKeysHolder export(StsKeyStore keyStore);
}
