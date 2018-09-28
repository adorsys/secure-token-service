package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.jwk.JWKSet;



public interface KeyRetrieverService {
     JWKSet retrieve(String audience) ;
}
