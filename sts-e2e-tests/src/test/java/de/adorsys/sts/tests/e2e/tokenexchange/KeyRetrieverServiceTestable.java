package de.adorsys.sts.tests.e2e.tokenexchange;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import lombok.SneakyThrows;

import java.net.URL;

public class KeyRetrieverServiceTestable implements KeyRetrieverService {
    @Override
    @SneakyThrows
    public JWKSet retrieve(String audience) {
        URL url = Resources.getResource("fixture/key_retriever_service.json");
        return JWKSet.parse(Resources.toString(url, Charsets.UTF_8));
    }
}
