package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class KeyRetrieverService {

    /**
     * The default HTTP connect timeout for JWK set retrieval, in
     * milliseconds. Set to 250 milliseconds.
     */
    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 250;


    /**
     * The default HTTP read timeout for JWK set retrieval, in
     * milliseconds. Set to 250 milliseconds.
     */
    private static final int DEFAULT_HTTP_READ_TIMEOUT = 250;


    /**
     * The default HTTP entity size limit for JWK set retrieval, in bytes.
     * Set to 50 KBytes.
     */
    private static final int DEFAULT_HTTP_SIZE_LIMIT = 50 * 1024;

    private final ResourceRetriever resourceRetriever;
    private final ResourceServerService resourceServerService;

    public KeyRetrieverService(
            ResourceServerService resourceServerService,
            ResourceServerManagementProperties resourceServerManagementProperties
    ) {
        this.resourceServerService = resourceServerService;

        ResourceServerManagementProperties.ResourceRetrieverProperties resourceRetriever = resourceServerManagementProperties.getResourceRetriever();

        this.resourceRetriever = new DefaultResourceRetriever(
                Optional.ofNullable(resourceRetriever.getHttpConnectTimeout()).orElse(DEFAULT_HTTP_CONNECT_TIMEOUT),
                Optional.ofNullable(resourceRetriever.getHttpReadTimeout()).orElse(DEFAULT_HTTP_READ_TIMEOUT),
                Optional.ofNullable(resourceRetriever.getHttpSizeLimit()).orElse(DEFAULT_HTTP_SIZE_LIMIT)
        );
    }

    public JWKSet retrieve(String audience) {
        ResourceServer resourceServer = resourceServerService.getForAudience(audience);

        return retrieveJwkSet(resourceServer.getJwksUrl());
    }

    private JWKSet retrieveJwkSet(String endpointUrl) {
        Resource res;
        try {
            res = resourceRetriever.retrieveResource(new URL(endpointUrl));
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't retrieve remote jwk set from: " + endpointUrl, e);
        }

        try {
            JWKSet.parse(res.getContent());
            return JWKSet.parse(res.getContent());
        } catch (java.text.ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
