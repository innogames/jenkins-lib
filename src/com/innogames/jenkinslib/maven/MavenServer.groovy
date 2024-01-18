package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'server')
class MavenServer {

	String id

	String url

	String username

	String password

	String privateKey

	String passphrase

	def filePermissions

	def directoryPermissions

	def configuration

	MavenServer() {}

	def toXML() {
		def writer = new StringWriter()
		JAXBContext context = JAXBContext.newInstance(this.class)
		Marshaller m = context.createMarshaller()
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
		m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE)
		m.marshal(this, writer)

		return writer.toString()

		def markup = new MarkupBuilder(writer)
		markup.server {
			id(this.id)
			url(this.url)
			username(this.username)
			password(this.password)
		}

		return writer.toString()
	}

}
