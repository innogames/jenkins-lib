import com.innogames.jenkinslib.container.Container
import com.innogames.jenkinslib.logger.LogLevel

def call(Object message, LogLevel level = null) {
	this.log(message, level)
}

def call(Object message, String level) {
	LogLevel logLevel = null
	if (level != null) {
		logLevel = LogLevel.valueOf(level)
	}
	this.log(message, logLevel)
}

private def log(Object message, LogLevel level = null) {
	def container = Container.getInstance()
	def logger = container.getLogger()
	logger.log(message, level)
}

def trace(Object message) {
	call(message, LogLevel.TRACE)
}

def debug(Object message) {
	call(message, LogLevel.DEBUG)
}

def info(Object message) {
	call(message, LogLevel.INFO)
}

def warn(Object message) {
	call(message, LogLevel.WARN)
}

def error(Object message) {
	call(message, LogLevel.ERROR)
}

def critical(Object message) {
	call(message, LogLevel.CRITICAL)
}
