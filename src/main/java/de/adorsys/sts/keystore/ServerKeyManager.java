package de.adorsys.sts.keystore;

/**
 * Holds the signature keys in different formats.
 *
 * @author fpo
 *
 */
public class ServerKeyManager {

    private final ServerKeysHolder serverKeysHolder;
    private final ServerKeyMap keyMap;

    public ServerKeyManager(ServerKeysHolder serverKeysHolder){
        this.serverKeysHolder = serverKeysHolder;
        this.keyMap = new ServerKeyMap(serverKeysHolder.getPrivateKeySet());
    }

    public ServerKeysHolder getServerKeysHolder() {
        return serverKeysHolder;
    }

    public ServerKeyMap getKeyMap() {
        return keyMap;
    }
}
