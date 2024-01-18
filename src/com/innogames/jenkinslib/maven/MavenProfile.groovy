package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'profile')
class MavenProfile {

	String id

	def activation

	@XmlJavaTypeAdapter(MapAdapter.class)
	Map<String, String> properties

	@XmlElementWrapper(name = 'repositories')
	@XmlElement(name = 'repository')
	List<MavenRepository> repositories

	@XmlElementWrapper(name = 'pluginRepositories')
	@XmlElement(name = 'pluginRepository')
	List<MavenRepository> pluginRepositories

	MavenProfile() {}

//	MavenProfile(String id, def activation, Map<String, String> properties, List<Map<String, Object>> repositories)


	def toXML() {
		def writer = new StringWriter()

		JAXBContext context = JAXBContext.newInstance(MavenProfile.class)
		Marshaller m = context.createMarshaller()
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
		m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE)
		m.marshal(this, writer)

		return writer.toString()

		def markup = new MarkupBuilder(writer)
		markup.profile {
			id(this.id)
			if (properties) {
				properties {
					for (def property in properties) {
						markup."$property.key"(property.value)
					}
				}
			}
			if (repositories) {
				repositories {
					for (def repository in repositories) {
						mkp.yieldUnescaped(repository.toXML())
					}
				}
			}
			if (pluginRepositories) {
				pluginRepositories {
					for (def repository in pluginRepositories) {
						mkp.yieldUnescaped(repository.toXML())
					}
				}
			}
		}

		return writer.toString()
	}

	static def fromMap(Map<String, Object> map) {
		def tmp = map.clone()
		tmp.repositories = tmp.repositories.collect { new MavenRepository(it) }
		tmp.pluginRepositories = tmp.pluginRepositories.collect { new MavenRepository(it) }
		return new MavenProfile(tmp)
	}

}
