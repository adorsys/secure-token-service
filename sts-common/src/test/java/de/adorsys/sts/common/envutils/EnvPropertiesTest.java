package de.adorsys.sts.common.envutils;

import de.adorsys.sts.common.tests.BaseMockitoTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvPropertiesTest extends BaseMockitoTest {

    private static final String PROP_NAME = "MY_SUPER_DUPER_PROPERTY";
    private static final String DEFAULT_PREFIX = "PREFIX-111-";
    private static final String[] BLANKS = {"", " ", "   ", "\t", "\t\t\t", "\n"};

    @AfterEach
    void unsetProperty() {
        System.getProperties().remove(PROP_NAME);
    }

    @ParameterizedTest
    @MethodSource("blanks")
    void getEnvOrSysPropAllBlanks(String value) {
        System.setProperty(PROP_NAME, value);

        assertThat(EnvProperties.getEnvOrSysProp(PROP_NAME, true)).isNull();
        assertThrows(IllegalStateException.class, () -> EnvProperties.getEnvOrSysProp(PROP_NAME, false));
    }

    @Test
    void getEnvOrSysPropValue() {
        String value = "MY-value-" + UUID.randomUUID();
        System.setProperty(PROP_NAME, value);

        assertThat(EnvProperties.getEnvOrSysProp(PROP_NAME, false)).isEqualTo(value);
        assertThat(EnvProperties.getEnvOrSysProp(PROP_NAME, true)).isEqualTo(value);
    }

    @ParameterizedTest
    @MethodSource("blanks")
    void testGetEnvOrSysPropAllBlanks(String value) {
        String defaultValue = DEFAULT_PREFIX + value;
        System.setProperty(PROP_NAME, DEFAULT_PREFIX + value);

        assertThat(EnvProperties.getEnvOrSysProp(PROP_NAME, defaultValue)).isEqualTo(defaultValue);
    }

    @Test
    void testGetEnvOrSysPropValue() {
        String value = "MY-value-" + UUID.randomUUID();
        System.setProperty(PROP_NAME, value);

        assertThat(EnvProperties.getEnvOrSysProp(PROP_NAME, "DEFAULT")).isEqualTo(value);
        assertThat(EnvProperties.getEnvOrSysProp(PROP_NAME, "DEFAULT")).isEqualTo(value);
    }

    private static Stream<String> blanks() {
        return Arrays.stream(BLANKS);
    }
}