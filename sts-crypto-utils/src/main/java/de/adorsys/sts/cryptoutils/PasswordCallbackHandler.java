package de.adorsys.sts.cryptoutils;

import lombok.SneakyThrows;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.util.Arrays;

public final class PasswordCallbackHandler implements CallbackHandler, AutoCloseable {

	private char[] password;

	public PasswordCallbackHandler(char[] password) {
        if (null == password) {
			return;
		}

		this.password = Arrays.copyOf(password, password.length);
	}

	@Override
	public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
		if (!(callbacks[0] instanceof PasswordCallback)) {
			throw new UnsupportedCallbackException(callbacks[0]);
		}

		PasswordCallback passwordCallback = (PasswordCallback) callbacks[0];
		passwordCallback.setPassword(this.password);
	}

	@Override
	@SneakyThrows
	public void close() {
		cleanup();
	}

	@Override
	protected void finalize() throws Throwable {
		cleanup();
		super.finalize();
	}

	private void cleanup() {
		if (null == this.password) {
			return;
		}

		Arrays.fill(this.password, ' ');
	}
}
