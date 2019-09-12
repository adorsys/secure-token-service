package de.adorsys.sts.tests;

import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class Resource {

    @SneakyThrows
    public String read(String path) {
        return Resources.toString(Resources.getResource(path), StandardCharsets.UTF_8);
    }
}
