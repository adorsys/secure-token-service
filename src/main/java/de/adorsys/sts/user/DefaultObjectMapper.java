package de.adorsys.sts.user;

import java.io.IOException;

import org.adorsys.encobject.userdata.ObjectMapperSPI;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultObjectMapper implements ObjectMapperSPI {
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public <T> T readValue(byte[] src, Class<T> klass) throws IOException {
		return objectMapper.readValue(src, klass);
	}

	@Override
	public <T> byte[] writeValueAsBytes(T t) throws IOException {
		return objectMapper.writeValueAsBytes(t);
	}

}
