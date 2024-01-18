package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'repository')
class MavenRepository {

	String id

	String name

	String url

	// default or legacy
	String layout

	MavenPolicy releases

	MavenPolicy snapshots

	MavenRepository() {
	}

//	MavenRepository(String id, String name, String url, String layout, Map<String, Object> releases, Map<String, Object> snapshots) {
//		this.id = id
//		this.name = name
//		this.url = url
//		this.layout = layout
//		if (releases != null) {
//			this.releases = new MavenPolicy(releases)
//		}
//		if (snapshots != null) {
//			this.snapshots = new MavenPolicy(snapshots)
//		}
//	}

	def toXML() {
		def writer = new StringWriter()

		JAXBContext context = JAXBContext.newInstance(MavenRepository.class)
		Marshaller m = context.createMarshaller()
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
		m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE)
//		m.setProperty(Marshaller.JAXB_ENCODING, '')
//		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, 'something')
		m.marshal(this, writer)

		return writer.toString()

		def markup = new MarkupBuilder(writer)
		markup.repository {
			id(this.id)
			name(this.name)
			url(this.url)
			if (releases != null) {
				mkp.yieldUnescaped(releases.toXML('releases'))
			}
			if (snapshots != null) {
				mkp.yieldUnescaped(snapshots.toXML('snapshots'))
			}
			if (layout != null) {
				layout(this.layout)
			}
		}

		return writer.toString()
	}

	boolean equals(Object other) {
		if (other == null) return false
		if (this.is(other)) return true
		if (!(other instanceof MavenRepository)) return false
		if (!other.canEqual(this)) return false
		if (id != other.id) return false
		if (name != other.name) return false
		if (url != other.url) return false
		if (layout != other.layout) return false
		if (releases != other.releases) return false
		if (snapshots != other.snapshots) return false
		return true
	}

	boolean canEqual(Object other) {
		return other instanceof MavenRepository
	}

}
