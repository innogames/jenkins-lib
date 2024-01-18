package com.innogames.jenkinslib.logger


import com.innogames.jenkinslib.container.Value
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Logger {

	CpsScript script

	LogLevel level

	LogLevel defaultLevel = LogLevel.INFO

	@Autowired
	Logger(CpsScript script, @Value(value = 'logging.level') LogLevel logLevel) {
		this.script = script
		this.level = logLevel ?: LogLevel.INFO
	}

	def log(Object message, LogLevel level = null) {
		if (level == null) {
			level = defaultLevel
		}

		if (level < this.level) {
			return
		}

		def msg = message
		if (!msg instanceof String) {
			msg = String.valueOf(msg)
		}
		DefaultGroovyMethods.println(msg)
		script.println(msg)
	}

	def trace(Object message) {
		log(message, LogLevel.TRACE)
	}

	def debug(Object message) {
		log(message, LogLevel.DEBUG)
	}

	def info(Object message) {
		log(message, LogLevel.INFO)
	}

	def warn(Object message) {
		log(message, LogLevel.WARN)
	}

	def error(Object message) {
		log(message, LogLevel.ERROR)
	}

	def critical(Object message) {
		log(message, LogLevel.CRITICAL)
	}

	def stdOut(Object message) {
		def msg = message
		if (!msg instanceof String) {
			msg = String.valueOf(msg)
		}

		script.echo(msg)
	}

	def stdErr(Object message) {
		def msg = message
		if (!msg instanceof String) {
			msg = String.valueOf(msg)
		}

		script.sh("echo '${msg}' 1>&2")
	}

}