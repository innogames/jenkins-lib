package com.innogames.jenkinslib.http.auth

import org.apache.http.HttpMessage

class BasicAuth implements AuthInterface {

	private String username

	private String password

	private BasicAuth(String username, String password) {
		this.username = username
		this.password = password
	}

	@Override
	void prepareRequest(HttpMessage request) {
		request.setHeader('Authentication', 'Basic ' + this.base64)
	}

	String getBase64() {
		return "${username}:${password}".bytes.encodeBase64().toString()
	}

	static BasicAuth fromUsernameAndPassword(String username, String password) {
		return new BasicAuth(username, password)
	}

	static BasicAuth fromBase64(String base64) {
		String credentials = base64.decodeBase64().toString().split(':')
		if (credentials.size() != 2) {
			throw new IllegalArgumentException('Wrong value for basic authentication')
		}

		return fromUsernameAndPassword(credentials[0], credentials[1])
	}


}
