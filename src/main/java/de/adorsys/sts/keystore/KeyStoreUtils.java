package de.adorsys.sts.keystore;

import org.adorsys.jkeygen.keystore.PasswordCallbackUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.security.auth.callback.CallbackHandler;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by alexg on 24.05.17.
 */
public class KeyStoreUtils {

    /**
     * Loads a key store. Given the store bytes, the store type
     *
     * @param in : the inputStream from which to read the keystore
     * @param storeId : The store id. This is passed to the callback handler to identify the requested password record.
     * @param storeType : the type of this key store. f null, the defaut java keystore type is used.
     * @param storePassSrc : the callback handler that retrieves the store password.
     * @throws KeyStoreException either NoSuchAlgorithmException or NoSuchProviderException
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore cannot be found
     * @throws CertificateException if any of the certificates in the keystore could not be loaded
     * @throws UnrecoverableKeyException if a password is required but not given, or if the given password was incorrect
     * @throws IOException if there is an I/O or format problem with the keystore data
     */
    public static KeyStore loadKeyStore(InputStream in, String storeId, String storeType, CallbackHandler storePassSrc) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, IOException {

        // Use default type if blank.
//        if (StringUtils.isBlank(storeType))storeType = "UBER";
    	if(StringUtils.isBlank(storeType)) throw new IllegalArgumentException("Missing keystore type");

    	KeyStore ks = null; 
    	if(StringUtils.equalsIgnoreCase("JKS", storeType)){//"JKS".equals(storeType)
    		ks = KeyStore.getInstance(storeType);
    	} else {
    		ks = KeyStore.getInstance(storeType, new BouncyCastleProvider());
    	}

        try {
            ks.load(in, PasswordCallbackUtils.getPassword(storePassSrc, storeId));
        } catch (IOException e) {
            // catch missing or wrong key.
            if(e.getCause()!=null && (e.getCause() instanceof UnrecoverableKeyException)){
                throw (UnrecoverableKeyException)e.getCause();
            } else if (e.getCause()!=null && (e.getCause() instanceof BadPaddingException)){
                throw new UnrecoverableKeyException(e.getMessage());
            }
            throw e;
        }
        return ks;
    }
}
