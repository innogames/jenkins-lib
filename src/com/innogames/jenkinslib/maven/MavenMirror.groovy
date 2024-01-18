package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'mirror')
class MavenMirror {

	String id

	String name

	String url

	// repositoryId (central), csv repositoryIds (repo1,repo2), wildcard (*), negative (!repo1)
	String mirrorOf

	def toXML() {
		def writer = new StringWriter()
		def markup = new MarkupBuilder(writer)
		markup.mirror {
			id(this.id)
			name(this.name)
			url(this.url)
			if (mirrorOf != null) {
				mirrorOf(this.mirrorOf)
			}
		}

		return writer.toString()
	}

}
