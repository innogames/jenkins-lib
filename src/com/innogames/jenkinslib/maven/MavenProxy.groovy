package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'proxy')
class MavenProxy {

	String id

	Boolean active

	String protocol

	String host

	String username

	String password

	String nonProxyHosts

	def toXML() {
		def writer = new StringWriter()
		def markup = new MarkupBuilder(writer)
		markup.mirror {
			id(this.id)
			active(this.active)
			protocol(this.protocol)
			host(this.host)
			username(this.username)
			password(this.password)
			nonProxyHosts(this.nonProxyHosts)
		}

		return writer.toString()
	}

}
