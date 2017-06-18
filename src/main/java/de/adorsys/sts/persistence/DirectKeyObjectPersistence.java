package de.adorsys.sts.persistence;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.params.EncParamSelector;
import org.adorsys.encobject.params.EncryptionParams;
import org.adorsys.encobject.service.BlobStoreConnection;
import org.adorsys.encobject.service.BlobStoreContextFactory;
import org.adorsys.encobject.service.ObjectNotFoundException;
import org.adorsys.encobject.service.UnknownContainerException;
import org.adorsys.encobject.service.WrongKeyCredentialException;
import org.adorsys.jjwk.selector.JWEEncryptedSelector;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEHeader.Builder;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;

import de.adorsys.sts.keystore.ServerKeyManager;

public class DirectKeyObjectPersistence {

	private DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();
	
	private BlobStoreConnection blobStoreConnection;

	public DirectKeyObjectPersistence(BlobStoreContextFactory blobStoreContextFactory) {
		this.blobStoreConnection = new BlobStoreConnection(blobStoreContextFactory);
	}
	
	public void storeObject(byte[] data, ContentMetaInfo metaIno, ObjectHandle handle, ServerKeyManager serverKeyManager, String keyID, EncryptionParams encParams) throws UnsupportedEncAlgorithmException, UnsupportedKeyLengthException, UnknownContainerException {
		// We accept empty meta info
		if(metaIno==null) metaIno=new ContentMetaInfo();
		
		// Retrieve the key.
		Key key = readKey(serverKeyManager, keyID);
		
		// Encryption params is optional. If not provided, we select an encryption param based on the key selected.
		if(encParams==null){
			encParams=EncParamSelector.selectEncryptionParams(key);
		}
		
		Builder headerBuilder = new JWEHeader.Builder(encParams.getEncAlgo(), encParams.getEncMethod()).keyID(keyID);

		// content type
		String contentTrype = metaIno.getContentTrype();
		if(StringUtils.isNotBlank(contentTrype)){
			headerBuilder = headerBuilder.contentType(contentTrype);
		}
		
		String zip = metaIno.getZip();
		if(StringUtils.isNotBlank(zip)){
			headerBuilder = headerBuilder.compressionAlgorithm(CompressionAlgorithm.DEF);
		} else {
			if(StringUtils.isNotBlank(contentTrype) && StringUtils.containsIgnoreCase(contentTrype, "text")){
				headerBuilder = headerBuilder.compressionAlgorithm(CompressionAlgorithm.DEF);
			}
		}
		
		Map<String, Object> addInfos = metaIno.getAddInfos();
		// exp
		if(metaIno.getExp()!=null){
			if(addInfos==null) addInfos = new HashMap<>();
			addInfos.put("exp", metaIno.getExp().getTime());
		}
		
		if(addInfos!=null){
			headerBuilder = headerBuilder.customParams(addInfos);
		}
		
		JWEHeader header = headerBuilder.build();
		
		JWEEncrypter jweEncrypter = JWEEncryptedSelector.geEncrypter(key, encParams.getEncAlgo(), encParams.getEncMethod());
		
		JWEObject jweObject = new JWEObject(header,new Payload(data));
		
		try {
			jweObject.encrypt(jweEncrypter);
		} catch (JOSEException e) {
			throw new IllegalStateException("Encryption error", e);
		}
		
		String jweEncryptedObject = jweObject.serialize();
		
		byte[] bytesToStore;
		try {
			bytesToStore = jweEncryptedObject.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unsupported content type", e);
		}
		blobStoreConnection.putBlob(handle, bytesToStore);

	}

	public byte[] loadObject(ObjectHandle handle, ServerKeyManager serverKeyManager) throws ObjectNotFoundException, WrongKeyCredentialException, UnknownContainerException{

		byte[] jweEncryptedBytes = blobStoreConnection.getBlob(handle);
		String jweEncryptedObject;
		try {
			jweEncryptedObject = IOUtils.toString(jweEncryptedBytes, "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException("Unsupported content type", e);
		}
		JWEObject jweObject;
		try {
			jweObject = JWEObject.parse(jweEncryptedObject);
		} catch (ParseException e) {
			throw new IllegalStateException("Can not parse jwe object", e);
		}
		String keyID = jweObject.getHeader().getKeyID();
		Key key = readKey(serverKeyManager, keyID);

		JWEDecrypter decrypter;
		try {
			decrypter = decrypterFactory.createJWEDecrypter(jweObject.getHeader(), key);
		} catch (JOSEException e) {
			throw new IllegalStateException("No suitable key found", e);
		}
		try {
			jweObject.decrypt(decrypter);
		} catch (JOSEException e) {
			throw new WrongKeyCredentialException(e);
		}
		return jweObject.getPayload().toBytes();
	}
	
	/*
	 * Retrieves the key with the given keyID from the keystore. The key password will be retrieved by
	 * calling the keyPassHandler.
	 */
	private Key readKey(ServerKeyManager serverKeyManager, String keyID) {
		return serverKeyManager.getKeyMap().getKey(keyID);
	}
}
