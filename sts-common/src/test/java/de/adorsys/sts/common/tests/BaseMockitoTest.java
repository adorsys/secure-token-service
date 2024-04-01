package de.adorsys.sts.common.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public abstract class BaseMockitoTest {

    private AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void validate() throws Exception {
        Mockito.validateMockitoUsage();
        closeable.close();
    }
}
