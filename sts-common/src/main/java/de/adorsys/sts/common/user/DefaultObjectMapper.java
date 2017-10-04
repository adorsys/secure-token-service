package de.adorsys.sts.common.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.adorsys.encobject.userdata.ObjectMapperSPI;

import java.io.IOException;

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
