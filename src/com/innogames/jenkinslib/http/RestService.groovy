package com.innogames.jenkinslib.http

import com.cloudbees.groovy.cps.NonCPS
import com.fasterxml.jackson.databind.ObjectMapper
import com.innogames.jenkinslib.http.auth.AuthInterface
import com.innogames.jenkinslib.logger.Logger
import groovy.json.JsonSlurper
import org.apache.http.HttpException
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.*
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RestService {

	Logger log

	@Autowired
	RestService(Logger log) {
		this.log = log
	}

	String get(String url, AuthInterface auth = null, int timeout = 30) {
		log.debug("Http::httpGet for $url")

		HttpGet request = new HttpGet(url)
		prepareRequest(request, auth)

		RequestConfig config = retrieveTimeoutConfig(timeout)

		HttpClient client = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(config)
			.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
			.build()

		HttpResponse response = this.sendRequest(request, client)
		log.debug(response.statusLine.toString())

		throwExceptionWhenRestFailed(response.getStatusLine(), request)

		def result = response.entity.content.text
		return new JsonSlurper().parseText(result)
	}

	String put(String url, Map<String, Object> payload, AuthInterface auth = null, int timeout = 30) {
		def json = new ObjectMapper().writeValueAsString(payload)
		log.debug("HttpRequest::httpPut for $url with content $json")

		HttpPut request = new HttpPut(url)
		prepareRequest(request, auth)
		request.setEntity(new StringEntity(json, "UTF-8"))

		RequestConfig config = retrieveTimeoutConfig(timeout)

		HttpClient client = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(config)
			.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
			.build()

		HttpResponse response = this.sendRequest(request, client)

		throwExceptionWhenRestFailed(response.getStatusLine(), request)

		def result = response.entity.content.text
		return new JsonSlurper().parseText(result)
	}

	String post(String url, Map<String, Object> payload, AuthInterface auth = null, int timeout = 30) {
		def json = new ObjectMapper().writeValueAsString(payload)
		log.debug("HttpRequest::httpPost for $url with content $json")

		HttpPost request = new HttpPost(url)
		request.setEntity(new StringEntity(json, "UTF-8"))

		prepareRequest(request, auth)

		RequestConfig config = retrieveTimeoutConfig(timeout)

		HttpClient client = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(config)
			.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
			.build()

		HttpResponse response = this.sendRequest(request, client)

		throwExceptionWhenRestFailed(response.getStatusLine(), request)

		def result = response.entity.content.text
		return new JsonSlurper().parseText(result)
	}

	String delete(String url, AuthInterface auth = null, int timeout = 30) {
		log.log("HttpRequest::httpDelete for $url")

		HttpDelete request = new HttpDelete(url)
		prepareRequest(request, auth)

		RequestConfig config = retrieveTimeoutConfig(timeout)

		HttpClient client = HttpClientBuilder
			.create()
			.setDefaultRequestConfig(config)
			.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
			.build()

		HttpResponse response = this.sendRequest(request, client)

		throwExceptionWhenRestFailed(response.getStatusLine(), request)

		def result = response.entity.content.text
		return new JsonSlurper().parseText(result)
	}

	String request(String method, String url) {
		HttpURLConnection connection = new URL(url).openConnection()
		connection.setRequestMethod(method)
		connection.outputStream
		if (connection.getResponseCode() != 200) {
			throw new RuntimeException('Could not fetch versions')
		}
	}

	private RequestConfig retrieveTimeoutConfig(int timeout) {
		RequestConfig.custom()
			.setConnectTimeout(timeout * 1000)
			.setConnectionRequestTimeout(timeout * 1000)
			.build()
	}

	void prepareRequest(HttpUriRequest request, AuthInterface auth = null) {
		if (auth) {
			auth.prepareRequest(request)
		}
	}

	private void throwExceptionWhenRestFailed(StatusLine status, HttpUriRequest request) {
		def url = request.getURI()
		def method = request.getMethod()
		if (status.getStatusCode() < 200 || status.getStatusCode() >= 300) {
			throw new HttpException(
				"[HttpRequest Exception] Rest call failed " +
					"(code ${status.getStatusCode()}) for ${url} for method ${method}.\n" +
					"Message: ${status.getReasonPhrase()}"
			)
		}
	}

	@NonCPS
	private HttpResponse sendRequest(HttpUriRequest request, HttpClient client) {
		return client.execute(request)
	}

}