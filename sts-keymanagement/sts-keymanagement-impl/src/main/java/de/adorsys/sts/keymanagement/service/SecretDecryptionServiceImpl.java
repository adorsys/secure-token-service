package de.adorsys.sts.keymanagement.service;

public class SecretDecryptionServiceImpl implements SecretDecryptionService {

    private final DecryptionService decryptionService;
    private final SecretProvider secretProvider;

    public SecretDecryptionServiceImpl(
            DecryptionService decryptionService,
            SecretProvider secretProvider
    ) {
        this.decryptionService = decryptionService;
        this.secretProvider = secretProvider;
    }

    @Override
    public String decryptSecretClaim() {
        String encryptedSecret = secretProvider.get();
        return decryptionService.decrypt(encryptedSecret);
    }
}
