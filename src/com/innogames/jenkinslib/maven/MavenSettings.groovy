package com.innogames.jenkinslib.maven

import groovy.transform.EqualsAndHashCode
import groovy.xml.MarkupBuilder

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.*

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = 'settings')
class MavenSettings {

	@XmlAttribute(name = 'xmlns')
	String xmlns = 'http://maven.apache.org/SETTINGS/1.1.0'

//	@XmlAttribute(name = 'xmlns:xsi')
//	def xmlns_xsi = 'http://www.w3.org/2001/XMLSchema-instance'
//
//	@XmlAttribute(name = 'xsi:schemaLocation')
//	def xmlns_schemaLocation = 'http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd'

	String localRepository

	Boolean interactiveMode

	Boolean offline

	@XmlElementWrapper(name = 'pluginGroups')
	@XmlElement(name = 'pluginGroup')
	List<String> pluginGroups

	@XmlElementWrapper(name = 'servers')
	@XmlElement(name = 'server')
	List<MavenServer> servers

	@XmlElementWrapper(name = 'mirrors')
	@XmlElement(name = 'mirror')
	List<MavenMirror> mirrors

	@XmlElementWrapper(name = 'proxies')
	@XmlElement(name = 'proxy')
	List<MavenProxy> proxies

	@XmlElementWrapper(name = 'profiles')
	@XmlElement(name = 'profile')
	List<MavenProfile> profiles

	@XmlElementWrapper(name = 'activeProfiles')
	@XmlElement(name = 'activeProfile')
	List<String> activeProfiles

	MavenSettings() {}

	def toXML() {
		def writer = new StringWriter()

		JAXBContext context = JAXBContext.newInstance(this.class)
		Marshaller m = context.createMarshaller()
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, 'http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd')
		m.marshal(this, writer)

		return writer.toString()

		def markup = new MarkupBuilder(writer)
		markup.settings(['xmlns': xmlns[''], 'xmlns:xsi': xmlns['xsi'], 'xmlns:schemaLocation': xmlns['schemaLocation']]) {
			if (localRepository != null) {
				localRepository(this.localRepository)
			}
			if (interactiveMode != null) {
				interactiveMode(this.interactiveMode)
			}
			if (offline != null) {
				offline(this.offline)
			}
			if (servers) {
				servers {
					for (def server in servers) {
						mkp.yieldUnescaped(server.toXML())
					}
				}
			}
			if (profiles) {
				profiles {
					for (def profile in profiles) {
						mkp.yieldUnescaped(profile.toXML())
					}
				}
			}
			if (activeProfiles) {
				activeProfiles {
					for (def profile in activeProfiles) {
						activeProfile(profile)
					}
				}
			}
			if (pluginGroups) {
				pluginGroups {
					for (def pluginGroup in pluginGroups) {
						markup.pluginGroup(pluginGroup)
					}
				}
			}
			if (mirrors) {
				mirrors {
					for (def mirror in mirrors) {
						mkp.yieldUnescaped(mirror.toXML())
					}
				}
			}
			if (proxies) {
				proxies {
					for (def proxy in proxies) {
						mkp.yieldUnescaped(proxy.toXML())
					}
				}
			}
		}

		return writer.toString()
	}

	static def fromMap(Map<String, Object> map) {
		Map<String, Object> tmp = map.clone()
		if (tmp.containsKey('servers')) {
			tmp.servers = tmp.servers.collect { new MavenServer(it) }
		}
		if (tmp.containsKey('profiles')) {
			tmp.profiles = tmp.profiles.collect { MavenProfile.fromMap(it) }
		}
		return new MavenSettings(tmp)
	}

	static def builder() {
		return new Builder()
	}

	static class Builder {

		def localRepository

		def servers = []

		def profiles

		def activeProfiles

		def pluginGroups

		Builder() {

		}

		def localRepository(String path) {
			this.localRepository = path
			return this
		}

		def addServer(MavenServer server) {
			this.servers.add(server)
			return this
		}

		def removeServer(MavenServer server) {
			this.servers.remove(server)
			return this
		}

		def removeServers(String id) {
			this.servers.removeIf({ MavenServer it -> it.id == id })
			return this
		}


	}

}
