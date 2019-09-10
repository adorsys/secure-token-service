package de.adorsys.sts.common.lock;

public interface LockClient {

    void executeIfOwned(String rotationLockName, Runnable toExecute);
}
