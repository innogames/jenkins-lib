package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'policy')
class MavenPolicy {

	Boolean enabled

	// updatePolicy always, daily, interval:X, never
	String updatePolicy

	// checksumPolicy always, daily, interval:X, never
	String checksumPolicy

	def toXML() {
		def writer = new StringWriter()

		JAXBContext context = JAXBContext.newInstance(MavenPolicy.class)
		Marshaller m = context.createMarshaller()
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
		m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE)
		m.marshal(this, writer)

		return writer.toString()

		def markup = new MarkupBuilder(writer)
		markup."$root" {
			markup.enabled(this.enabled)
			markup.updatePolicy(this.updatePolicy)
			markup.checksumPolicy(this.checksumPolicy)
		}

		return writer.toString()
	}

	boolean equals(Object other) {
		if (other == null) return false
		if (this.is(other)) return true
		if (!(other instanceof MavenPolicy)) return false
		if (!other.canEqual(this)) return false
		if (enabled != other.enabled) return false
		if (updatePolicy != other.updatePolicy) return false
		if (checksumPolicy != other.checksumPolicy) return false
		return true
	}

	boolean canEqual(Object other) {
		return other instanceof MavenPolicy
	}

}
