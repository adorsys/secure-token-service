package de.adorsys.sts.cryptoutils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class KeyValue<T> {

	@NonNull
    private final String key;

	private final T value;
	
	public boolean isNull() {
		return null == value;
	}
}
