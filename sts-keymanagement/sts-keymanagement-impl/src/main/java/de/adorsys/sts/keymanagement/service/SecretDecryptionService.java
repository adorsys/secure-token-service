package de.adorsys.sts.keymanagement.service;

public class SecretDecryptionService {

    private final DecryptionService decryptionService;
    private final SecretProvider secretProvider;

    public SecretDecryptionService(
            DecryptionService decryptionService,
            SecretProvider secretProvider
    ) {
        this.decryptionService = decryptionService;
        this.secretProvider = secretProvider;
    }

    public String decryptSecretClaim() {
        String encryptedSecret = secretProvider.get();
        return decryptionService.decrypt(encryptedSecret);
    }
}
