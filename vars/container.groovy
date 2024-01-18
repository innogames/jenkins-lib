import com.innogames.jenkinslib.container.Container
import org.jenkinsci.plugins.workflow.cps.CpsScript

def call(CpsScript script = null, Map<String, Object> config = [:]) {
	def container = Container.getInstance()
	if (container == null) {
		container = Container.newInstance(script, config)
	}

	return container
}
