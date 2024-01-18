package com.innogames.jenkinslib.io


import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.jenkinsci.plugins.workflow.cps.EnvActionImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EnvService {

	CpsScript script

	EnvActionImpl env

	@Autowired
	EnvService(CpsScript script) {
		this.script = script
		this.env = script.env
	}

	def get(String envVar) {
		return env.getProperty(envVar)
	}

	def set(String envVar, String value) {
		return env.setProperty(envVar, value)
	}

	def set(Map<String, String> envValues) {
		envValues.each {
			env.setProperty(it.key, it.value)
		}
	}

}
