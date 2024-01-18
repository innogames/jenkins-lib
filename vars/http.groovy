#!/usr/bin/groovy
import com.innogames.jenkinslib.container.Container
import com.innogames.jenkinslib.http.HttpService

static def call(String method, String url) {
	def httpService = Container.getInstance().getComponent(HttpService)
	return httpService.request(method, url)
}


static String get(String url) {
	def httpService = Container.getInstance().getComponent(HttpService)
	return httpService.httpGet(url)
}

static String post(String url, String payload) {
	def httpService = Container.getInstance().getComponent(HttpService)
	return httpService.httpPost(url, payload)
}

static String put(String url, String payload) {
	def httpService = Container.getInstance().getComponent(HttpService)
	return httpService.httpPut(url, payload)
}

static String delete(String url) {
	def httpService = Container.getInstance().getComponent(HttpService)
	return httpService.httpDelete(url)
}
