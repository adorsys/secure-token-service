package de.adorsys.sts.secretserver.encryption;

import de.adorsys.sts.objectmapper.JacksonObjectMapper;
import de.adorsys.sts.secret.Secret;
import de.adorsys.sts.secret.SecretRepository;
import de.adorsys.sts.simpleencryption.ObjectEncryption;
import de.adorsys.sts.simpleencryption.StaticKeyEncryptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EncryptedSecretRepositoryTest {

    private static final String ALG = "A256GCMKW";
    private static final String ENC = "A256GCM";
    private static final String KEY = "{\"kty\":\"oct\",\"kid\":\"e3836555-588c-47fb-b148-948baa56b437\",\"k\":\"pqkSPSJqTBCUhp2JuYEKweJNHsF7tkll7an-L7XpiPk\",\"alg\":\"A256GCM\"}";
    private static final String SUBJECT = "my subject";
    private static final String SECRET = "my secret";

    private SecretRepository encryptedSecretRepository;

    @Mock
    private SecretRepository mockedRepository;

    @Captor
    ArgumentCaptor<Secret> secretCaptor;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        StaticKeyEncryptionFactory factory = new StaticKeyEncryptionFactory(new JacksonObjectMapper());
        ObjectEncryption objectEncryption = factory.create(ALG, ENC, KEY);

        encryptedSecretRepository = new EncryptedSecretRepository(mockedRepository, objectEncryption);
    }

    @Test
    public void shouldGetDecryptedSecret() throws Exception {
        encryptedSecretRepository.save(SUBJECT, new Secret(SECRET));

        Mockito.verify(mockedRepository, Mockito.times(1)).save(ArgumentMatchers.eq(SUBJECT), secretCaptor.capture());

        Mockito.when(mockedRepository.get(SUBJECT)).thenReturn(secretCaptor.getValue());
        Mockito.when(mockedRepository.tryToGet(SUBJECT)).thenReturn(Optional.of(secretCaptor.getValue()));

        Secret secret = encryptedSecretRepository.get(SUBJECT);

        assertThat(secret.getValue(), is(equalTo(SECRET)));
    }

    @Test
    public void shouldGetDecryptedSecretWhenUseTryMethod() throws Exception {
        encryptedSecretRepository.save(SUBJECT, new Secret(SECRET));

        Mockito.verify(mockedRepository, Mockito.times(1)).save(ArgumentMatchers.eq(SUBJECT), secretCaptor.capture());

        Mockito.when(mockedRepository.get(SUBJECT)).thenReturn(secretCaptor.getValue());
        Mockito.when(mockedRepository.tryToGet(SUBJECT)).thenReturn(Optional.of(secretCaptor.getValue()));

        Optional<Secret> secret = encryptedSecretRepository.tryToGet(SUBJECT);

        assertThat(secret.get().getValue(), is(equalTo(SECRET)));
    }
}
