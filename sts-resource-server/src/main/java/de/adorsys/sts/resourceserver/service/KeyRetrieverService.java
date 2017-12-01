package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.io.IOException;
import java.net.URL;

public class KeyRetrieverService {

    /**
     * The default HTTP connect timeout for JWK set retrieval, in
     * milliseconds. Set to 250 milliseconds.
     */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 250;


    /**
     * The default HTTP read timeout for JWK set retrieval, in
     * milliseconds. Set to 250 milliseconds.
     */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 250;


    /**
     * The default HTTP entity size limit for JWK set retrieval, in bytes.
     * Set to 50 KBytes.
     */
    public static final int DEFAULT_HTTP_SIZE_LIMIT = 50 * 1024;

    private final ResourceRetriever resourceRetriever = new DefaultResourceRetriever(
            DEFAULT_HTTP_CONNECT_TIMEOUT,
            DEFAULT_HTTP_READ_TIMEOUT,
            DEFAULT_HTTP_SIZE_LIMIT
    );

    private final ResourceServerService resourceServerService;

    public KeyRetrieverService(ResourceServerService resourceServerService) {
        this.resourceServerService = resourceServerService;
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
            throw new IllegalStateException("Couldn't retrieve remote metadata: " + e.getMessage(), e);
        }

        try {
            JWKSet.parse(res.getContent());
            return JWKSet.parse(res.getContent());
        } catch (java.text.ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
