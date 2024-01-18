package com.innogames.jenkinslib.maven

import groovy.xml.MarkupBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MavenService {

	@Autowired
	MavenService() {

	}

	def buildSettings(MavenSettings settings) {
		def markup = new MarkupBuilder()
		markup.settings {

		}
	}

}
