package ddc.template.datagrid;

import java.io.Closeable;
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.marshall.jboss.GenericJBossMarshaller;

public class CacheManagerConnection implements Closeable {
	private String username = null;
	private String password = null;
	private String host = null;
	private int port = ConfigurationProperties.DEFAULT_HOTROD_PORT;
	private RemoteCacheManager manager = null;

	public CacheManagerConnection(String host, int port, String username, String password) {
		this.host = host;
		this.host = host;
		this.username = username;
		this.password = password;
	}

	public CacheManagerConnection(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}

	private RemoteCache<String, Object> getCache(String cacheName) {
		if (manager == null) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.addServer().host(host).port(port);
			builder.marshaller(GenericJBossMarshaller.class);
			if (username != null && password != null) {
				builder.security()
			    		.authentication()
			    		.enable()
			    		.serverName(host)
			    		.saslMechanism("PLAIN")
			    		.realm("ApplicationRealm")
			    		.username(username).password(password);
//			    		.callbackHandler(new TestCallbackHandler(username, "ApplicationRealm", password.toCharArray()));					
			}
			manager = new RemoteCacheManager(builder.build());
			manager.start();

		}
		return manager.getCache(cacheName);
	}

	public static class TestCallbackHandler implements CallbackHandler {
		final private String username;
		final private char[] password;
		final private String realm;

		public TestCallbackHandler(String username, String realm, char[] password) {
			this.username = username;
			this.password = password;
			this.realm = realm;
		}

		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					NameCallback nameCallback = (NameCallback) callback;
					nameCallback.setName(username);
				} else if (callback instanceof PasswordCallback) {
					PasswordCallback passwordCallback = (PasswordCallback) callback;
					passwordCallback.setPassword(password);
				} else if (callback instanceof AuthorizeCallback) {
					AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
					authorizeCallback.setAuthorized(authorizeCallback.getAuthenticationID().equals(authorizeCallback.getAuthorizationID()));
				} else if (callback instanceof RealmCallback) {
					RealmCallback realmCallback = (RealmCallback) callback;
					realmCallback.setText(realm);
				} else {
					throw new UnsupportedCallbackException(callback);
				}
			}
		}

	}

	public void put(String cacheName, String key, Object value) {
		RemoteCache<String, Object> cache = getCache(cacheName);
		cache.put(key, value);
	}

	public Object get(String cacheName, String key) {
		RemoteCache<String, Object> cache = getCache(cacheName);
		return cache.get(key);
	}

	public void close() throws IOException {
		if (manager != null) {
			manager.stop();
		}
	}

}
