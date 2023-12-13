//package de.adorsys.sts.tokenauth;
//
//import com.nimbusds.jose.RemoteKeySourceException;
//import com.nimbusds.jose.jwk.JWK;
//import com.nimbusds.jose.jwk.JWKSelector;
//import com.nimbusds.jose.jwk.RSAKey;
//import com.nimbusds.jose.jwk.source.JWKSource;
//import com.nimbusds.jose.jwk.source.RemoteJWKSet;
//import com.nimbusds.jose.proc.SecurityContext;
//import com.nimbusds.jose.util.Base64URL;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.security.Key;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.interfaces.RSAPublicKey;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//class AuthServerTest {
//
//    private AuthServer authServer;
//    private RemoteJWKSet<SecurityContext> mockRemoteJWKSet;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        authServer = new AuthServer("TestServer", "https://example.com/iss", "https://example.com/jwks", 10);
//        mockRemoteJWKSet = Mockito.mock(RemoteJWKSet.class);
//        JWK jwk = new RSAKey.Builder(new Base64URL("n"), new Base64URL("e")).keyID("testKey").build();
//        when(mockRemoteJWKSet.get(any(JWKSelector.class), any(SecurityContext.class))).thenReturn(Collections.singletonList(jwk));
//    }
//
//    @Test
//    void testCacheInitialization() {
//        assertTrue(authServer.jwkCache.isEmpty(), "Cache should be initially empty");
//    }
//
//    @Test
//    void testCacheUpdateAfterInterval() throws Exception {
//        // Simulieren, dass die letzte Aktualisierung lange zurückliegt
//        JWKSource<SecurityContext> mockJwkSource = Mockito.mock(JWKSource.class);
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(512); // 512-bit RSA key pair
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        // Create a mock RSAKey from the generated key pair
//        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).keyID("testKey").build();
//
//        // Configure your AuthServer instance
//        AuthServer authServer = new AuthServer("TestServer", "https://example.com/iss", "https://example.com/jwks");
//        authServer.setJwkSource(mockJwkSource); // Inject the mock
//
//        // Mock the JWKSource and configure it to return the mock RSAKey
//        Mockito.when(mockJwkSource.get(any(), any())).thenReturn(Collections.singletonList(rsaKey));
//        authServer.lastCacheUpdate = 0;
//        authServer.getJWK("testKey");
//
//        assertFalse(authServer.jwkCache.isEmpty(), "Cache should be updated after interval");
//    }
//
//    @Test
//    void testCacheUpdateOnNonExistingKey() {
//        assertThrows(AuthServer.JsonWebKeyRetrievalException.class, () -> authServer.getJWK("nonExistingKey"));
//    }
//
//    @Test
//    void testValidKeyRetrieval() throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(512); // 512-bit RSA key pair
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//
//        // Create a mock RSAKey from the generated key pair
//        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).keyID("testKey").build();
//
//        // Mock the JWKSource and configure it to return the mock RSAKey
//        JWKSource<SecurityContext> mockJwkSource = Mockito.mock(JWKSource.class);
//        Mockito.when(mockJwkSource.get(any(), any())).thenReturn(Collections.singletonList(rsaKey));
//
//        // Inject the mock JWKSource into your AuthServer
//        AuthServer authServer = new AuthServer("TestServer", "https://example.com/iss", "https://example.com/jwks");
//        // Assuming you have a method to set the JWKSource
//        authServer.setJwkSource(mockJwkSource);
//        // Now you can test your method
//        Key key = authServer.getJWK("testKey");
//
//        assertNotNull(key, "Should return a valid key for a valid keyID");
//    }
//
//    @Test
//    void testExceptionHandling() throws RemoteKeySourceException {
//        // Konfigurieren Sie das Mock-Objekt, um eine Ausnahme zu werfen
//        when(mockRemoteJWKSet.get(any(JWKSelector.class), any(SecurityContext.class))).thenThrow(new RuntimeException("Test Exception"));
//
//        assertThrows(AuthServer.JsonWebKeyRetrievalException.class, () -> authServer.getJWK("testKey"));
//    }
//
//
//}