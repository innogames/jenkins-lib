package com.innogames.jenkinslib.http.auth

import org.apache.http.HttpMessage

interface AuthInterface {

	void prepareRequest(HttpMessage request)

}