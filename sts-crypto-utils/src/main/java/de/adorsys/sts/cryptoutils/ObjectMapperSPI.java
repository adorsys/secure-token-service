package de.adorsys.sts.cryptoutils;

import java.io.IOException;
import java.util.Map;

public interface ObjectMapperSPI {

	<T> T readValue(byte[] src, Class<T> klass) throws IOException;

    <T> T readValue(String src, Class<T> klass) throws IOException;
	Map<String, String> readValue(String src) throws IOException;

	<T> byte[] writeValueAsBytes(T t) throws IOException;
	<T> String writeValueAsString(T t) throws IOException;
}
