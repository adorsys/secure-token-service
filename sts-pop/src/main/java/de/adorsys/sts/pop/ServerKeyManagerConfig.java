package de.adorsys.sts.pop;

import com.nimbusds.jose.jwk.JWKSet;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.service.*;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.serverkey.*;
import org.adorsys.jkeygen.keypair.KeyPairBuilder;
import org.adorsys.jkeygen.keypair.SelfSignedKeyPairData;
import org.adorsys.jkeygen.keystore.KeyPairData;
import org.adorsys.jkeygen.keystore.KeystoreBuilder;
import org.adorsys.jkeygen.keystore.SecretKeyData;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.adorsys.jkeygen.secretkey.SecretKeyBuilder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.UUID;


@Configuration
public class ServerKeyManagerConfig {

	@Autowired
	private FsPersistenceFactory persFactory;

    private ServerKeyManager serverKeyManager;
    
    /**
     * Read the masterId and master password from environment properties.
     * <p>
     * If this information is not available, we will generate it and store it a a common location
     * where all server can read.
     * <p>
     * In order for the server to be restarted, we will need those information either as part
     * of the environment properties, or available on a dedicated file system.
     */
    @PostConstruct
    public void initServerKeyManager() {
    	
    	ContainerPersistence containerPersistence = persFactory.getContainerPersistence();
        String serverKeystoreContainer = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_CONTAINER, "adsts-container");
        if(!containerPersistence.containerExists(serverKeystoreContainer)){
        	try {
				containerPersistence.creteContainer(serverKeystoreContainer);
			} catch (ContainerExistsException e) {
				throw new IllegalStateException(e);
			}
        }
        String serverKeystoreName = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_NAME, "adsts-keystore");
        String serverKeyPairName = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYPAIR_NAME, "Adorsys Security Token Service");
        String serverKeyPairAliasPrefix = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYALIAS_PREFIX, "adsts-");

        String serverKeystorePassword = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.KEYSTORE_PASSWORD, true);
        if (StringUtils.isBlank(serverKeystorePassword))
            throw new IllegalStateException("Missing environment property KEYSTORE_PASSWORD");
        
        String resetKeystore = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.RESET_KEYSTORE, true);
        KeyStore keyStore;
        char[] keystorePassword = serverKeystorePassword.toCharArray();
        char[] keyPairPassword = serverKeystorePassword.toCharArray();
        CallbackHandler keyPassHandler = new PasswordCallbackHandler(keystorePassword);
        CallbackHandler storePassHandler = new PasswordCallbackHandler(keyPairPassword);

        ObjectHandle handle = new ObjectHandle(serverKeystoreContainer, serverKeystoreName);

        if(BooleanUtils.toBoolean(resetKeystore)){
        	keyStore = createKeystore(serverKeystoreName, storePassHandler, serverKeyPairName, serverKeyPairAliasPrefix, keyPassHandler, handle);
        } else {
            try {
                keyStore = persFactory.getKeystorePersistence().loadKeystore(handle, storePassHandler);
            } catch (ObjectNotFoundException e) {
            	keyStore = createKeystore(serverKeystoreName, storePassHandler, serverKeyPairName, serverKeyPairAliasPrefix, keyPassHandler, handle);
            } catch (CertificateException | WrongKeystoreCredentialException
                    | MissingKeystoreAlgorithmException | MissingKeystoreProviderException | MissingKeyAlgorithmException
                    | IOException | UnknownContainerException e) {
                throw new IllegalStateException(e);
            }
        }


        JWKSet privateKeys = KeyConverter.exportPrivateKeys(keyStore, keyPairPassword);
        JWKSet publicKeys = privateKeys.toPublicJWKSet();
        ServerKeysHolder serverKeysHolder = new ServerKeysHolder(privateKeys, publicKeys);
        serverKeyManager = new ServerKeyManager(serverKeysHolder);
    }

    private static int[] keyUsageSignature = {KeyUsage.nonRepudiation};
    private static int[] keyUsageEncryption = {KeyUsage.keyEncipherment, KeyUsage.dataEncipherment, KeyUsage.keyAgreement};
    
    private KeyStore createKeystore(String serverKeystoreName, CallbackHandler storePassHandler, String serverKeyPairName, String serverKeyPairAliasPrefix, CallbackHandler keyPassHandler, ObjectHandle handle){
    	KeyStore keyStore;
        String signKeyCountStr = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_SIGN_KEY_COUNT, "5");
        int signKeyCount = Integer.parseInt(signKeyCountStr);
        
        String encKeyCountStr = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_ENCRYPT_KEY_COUNT, "5");
        int encKeyCount = Integer.parseInt(encKeyCountStr);

        String secretKeyCountStr = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_SECRET_KEY_COUNT, "5");
        int secKeyCount = Integer.parseInt(secretKeyCountStr);

        keyStore = newKeystore(signKeyCount, encKeyCount, secKeyCount, serverKeystoreName, keyPassHandler, serverKeyPairName, serverKeyPairAliasPrefix);
        try {
        	persFactory.getKeystorePersistence().saveKeyStore(keyStore, storePassHandler, handle);
		} catch (NoSuchAlgorithmException | CertificateException | UnknownContainerException e) {
            throw new IllegalStateException(e);
		}    	
        
        return keyStore;
    }
    

    
    private KeyStore newKeystore(int numberOfSignKeypairs, int numberOfEncKeypairs, int numberOfSecretKeys, String serverKeystoreName, CallbackHandler storePassHandler, String serverKeyPairName, String serverKeyPairAliasPrefix) {
        try {
        	// UBER
        	String keystoreType = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_TYPE, "UBER");// UBER
            KeystoreBuilder keystoreBuilder = new KeystoreBuilder().withStoreType(keystoreType);
            for (int i = 0; i < numberOfSignKeypairs; i++) {
                keystoreBuilder = keystoreBuilder.withKeyEntry(newKeyPair(serverKeyPairName,
                        serverKeyPairAliasPrefix + UUID.randomUUID().toString(),
                        storePassHandler, keyUsageSignature));
            }
            for (int i = 0; i < numberOfEncKeypairs; i++) {
                keystoreBuilder = keystoreBuilder.withKeyEntry(newKeyPair(serverKeyPairName,
                        serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
                        storePassHandler, keyUsageEncryption));
            }
            for (int i = 0; i < numberOfSecretKeys; i++) {
                keystoreBuilder = keystoreBuilder.withKeyEntry(newSecretKey(
                		serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase(), 
                		storePassHandler));
            }
            byte[] bs = keystoreBuilder.withStoreId(serverKeystoreName).build(storePassHandler);

            ByteArrayInputStream bis = new ByteArrayInputStream(bs);
            return KeyStoreUtils.loadKeyStore(bis, serverKeystoreName, keystoreType, storePassHandler);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private KeyPairData newKeyPair(String userName, String alias, CallbackHandler keyPassHandler, int[] keyUsages) {
    	String keyAlgo = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_KEYPAIR_ALGO, "RSA");// RSA
    	String keySizeStr = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_KEYPAIR_SIZE, "2048");// 2048
    	String serverSigAlgo = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_RSA_SIGN_ALGO, "SHA256withRSA"); // SHA1withRSA
    	int keySize = Integer.parseInt(keySizeStr);
        KeyPair keyPair = new KeyPairBuilder().withKeyAlg(keyAlgo).withKeyLength(keySize).build();
        X500Name dn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, userName).build();
        SelfSignedKeyPairData keyPairData = new SingleKeyUsageSelfSignedCertBuilder()
        		.withSubjectDN(dn)
                .withSignatureAlgo(serverSigAlgo)
                .withNotAfterInDays(900)
                .withCa(false)
                .withKeyUsages(keyUsages)
                .build(keyPair);
        return new KeyPairData(keyPairData, null, alias, keyPassHandler);
    }
    
	public static SecretKeyData newSecretKey(String alias, CallbackHandler secretKeyPassHandler){
    	String secretKeyAlgo = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_SECRET_KEY_ALGO, "AES");// AES
    	String secretKeySizeStr = EnvProperties.getEnvOrSysProp(ServerKeyPropertiesConstants.SERVER_KEYSTORE_SECRET_KEY_SIZE, "256");// 256
    	int keySize = Integer.parseInt(secretKeySizeStr);
		SecretKey secretKey = new SecretKeyBuilder().withKeyAlg(secretKeyAlgo).withKeyLength(keySize).build();	
		return new SecretKeyData(secretKey, alias, secretKeyPassHandler);
	}

    @Bean
    public ServerKeyManager getServerKeyManager() {
        return serverKeyManager;
    }
}
