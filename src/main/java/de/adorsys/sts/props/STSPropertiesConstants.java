package de.adorsys.sts.props;

public class STSPropertiesConstants {

	public static final String RESET_KEYSTORE = "RESET_KEYSTORE";
	public static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";
	
	
	public static final String RESOURCE_SERVER_AUDIENCE_NAME_SUFFIX = "_AUDIENCE_NAME";
	public static final String RESOURCE_SERVER_USER_SECRET_CLAIM_SUFFIX = "_USER_SECRET_CLAIM";
	public static final String RESOURCE_SERVER_CLIENT_ID_SUFFIX = "_CLIENT_ID";
	public static final String RESOURCE_SERVER_JWKS_URL_SUFFIX = "_JWKS_URL";
	public static final String RESOURCE_SERVER_ENDPOINT_URL_SUFFIX = "_ENDPOINT_URL";
	public static final String RESOURCE_SERVER_NAMES = "RESOURCE_SERVER_NAMES";
	
	
	public static final String SERVER_KEYSTORE_SECRET_KEY_SIZE = "SERVER_KEYSTORE_SECRET_KEY_SIZE";// 256
	public static final String SERVER_KEYSTORE_SECRET_KEY_ALGO = "SERVER_KEYSTORE_SECRET_KEY_ALGO"; // AES
	public static final String SERVER_KEYSTORE_RSA_SIGN_ALGO = "SERVER_KEYSTORE_RSA_SIGN_ALGO";// SHA1withRSA

	public static final String SERVER_KEYSTORE_KEYPAIR_SIZE = "SERVER_KEYSTORE_KEYPAIR_SIZE";// 2048
	public static final String SERVER_KEYSTORE_KEYPAIR_ALGO = "SERVER_KEYSTORE_KEYPAIR_ALGO";// RSA
	public static final String SERVER_KEYSTORE_TYPE = "SERVER_KEYSTORE_TYPE";// UBER

	public static final String SERVER_SECRET_KEY_COUNT = "SERVER_SECRET_KEY_COUNT";// 5
	public static final String SERVER_ENCRYPT_KEY_COUNT = "SERVER_ENCRYPT_KEY_COUNT";// 5
	public static final String SERVER_SIGN_KEY_COUNT = "SERVER_SIGN_KEY_COUNT";// 5
	
	
	public static final String SERVER_KEYALIAS_PREFIX = "SERVER_KEYALIAS_PREFIX"; // "adsts-"
	public static final String SERVER_KEYPAIR_NAME = "SERVER_KEYPAIR_NAME"; // "Adorsys Security Token Service"
	public static final String SERVER_KEYSTORE_NAME = "SERVER_KEYSTORE_NAME";//"adsts-keystore"
	public static final String SERVER_KEYSTORE_CONTAINER = "SERVER_KEYSTORE_CONTAINER";// "adsts-container"
}
