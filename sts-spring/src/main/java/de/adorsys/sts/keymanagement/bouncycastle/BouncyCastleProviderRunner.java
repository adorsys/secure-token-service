package de.adorsys.sts.keymanagement.bouncycastle;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.crypto.NoSuchPaddingException;
import java.security.Provider;
import java.security.Security;

@Component
public class BouncyCastleProviderRunner implements ApplicationRunner {
    private static final String BOUNCY_CASTLE_PROVIDER_NAME = "BC";

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Provider bcProvider = Security.getProvider(BOUNCY_CASTLE_PROVIDER_NAME);

        if(bcProvider==null) {
            throw new IllegalStateException( new NoSuchPaddingException(BOUNCY_CASTLE_PROVIDER_NAME));
        }
    }
}
