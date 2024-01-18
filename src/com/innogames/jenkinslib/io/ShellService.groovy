package com.innogames.jenkinslib.io


import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShellService {

	CpsScript script

	@Autowired
	ShellService(CpsScript script) {
		this.script = script
	}

	int sh(String command) {
		return script.sh(script: command, returnStatus: true)
	}

	String shVerbose(String command) {
		return script.sh(script: command, returnStdout: true)
	}

	String pwd() {
		return shVerbose('pwd')
	}

}
