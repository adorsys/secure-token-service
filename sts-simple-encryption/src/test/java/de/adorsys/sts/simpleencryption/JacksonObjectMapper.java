package de.adorsys.sts.simpleencryption;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.sts.cryptoutils.ObjectMapperSPI;

import java.io.IOException;
import java.util.Map;

public class JacksonObjectMapper implements ObjectMapperSPI {
    private final TypeReference<Map<String, String>> STRING_MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public <T> T readValue(byte[] src, Class<T> klass) throws IOException {
        return objectMapper.readValue(src, klass);
    }

    @Override
    public <T> T readValue(String s, Class<T> klass) throws IOException {
        return objectMapper.readValue(s, klass);
    }

    @Override
    public Map<String, String> readValue(String s) throws IOException {
        return objectMapper.readValue(s, STRING_MAP_TYPE_REFERENCE);
    }

    @Override
    public <T> byte[] writeValueAsBytes(T t) throws IOException {
        return objectMapper.writeValueAsBytes(t);
    }

    @Override
    public <T> String writeValueAsString(T t) throws IOException {
        return objectMapper.writeValueAsString(t);
    }
}
