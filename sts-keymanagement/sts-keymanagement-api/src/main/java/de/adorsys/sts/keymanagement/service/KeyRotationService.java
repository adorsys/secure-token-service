package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.KeyRotationResult;
import de.adorsys.sts.keymanagement.model.StsKeyStore;

public interface KeyRotationService {

    KeyRotationResult rotate(StsKeyStore stsKeyStore);
}
