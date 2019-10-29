package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.types.ResultCollection;
import de.adorsys.keymanagement.api.types.entity.KeyEntry;
import de.adorsys.sts.common.converter.KeyConverter;
import de.adorsys.sts.keymanagement.model.ServerKeysHolder;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.model.UnmodifyableKeyStoreViewer;
import de.adorsys.sts.keymanagement.model.UnmodifyableKeystore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class KeyConversionServiceImpl implements KeyConversionService {

    private final String keyStorePassword;

    @Override
    @SneakyThrows
    public ServerKeysHolder export(StsKeyStore keyStore) {
        KeyStore toParse = new UnmodifyableKeyStoreViewer(keyStore.getKeyStoreCopy()).getKeyStore();
        // This excludes metadata keys:
        ResultCollection<KeyEntry> keyEntries = keyStore.getView().all();
        List<JWK> keys = new ArrayList<>();
        for (KeyEntry entry : keyEntries) {
            keys.add(JWK.load(toParse, entry.getAlias(), keyStorePassword.toCharArray()));
        }

        JWKSet privateKeys = new JWKSet(keys);
        JWKSet publicKeys = privateKeys.toPublicJWKSet();

        return new ServerKeysHolder(privateKeys, publicKeys);
    }
}
