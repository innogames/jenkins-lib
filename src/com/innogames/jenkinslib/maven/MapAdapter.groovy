package com.innogames.jenkinslib.maven

import javax.naming.OperationNotSupportedException
import javax.xml.bind.JAXBElement
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAnyElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlAdapter
import javax.xml.namespace.QName

public class MapAdapter extends XmlAdapter<MapAdapter.MapWrapper, Map<String, Object>> {

	@Override
	public MapWrapper marshal(Map<String, Object> m) throws Exception {
		if (m == null) {
			return null
		}
		MapWrapper wrapper = new MapWrapper();
		List<Object> elements = new ArrayList<>();
		for (Map.Entry<String, Object> property : m.entrySet()) {
			if (property.getValue() instanceof Map) {
				elements.add(
					new JAXBElement<>(new QName(getCleanLabel(property.getKey())),
						MapWrapper.class, marshal((Map) property.getValue()))
				);
			} else {
				elements.add(
					new JAXBElement<>(new QName(getCleanLabel(property.getKey())),
						String.class, property.getValue().toString())
				);
			}
		}
		wrapper.elements = elements;
		return wrapper;
	}

	@Override
	public Map<String, Object> unmarshal(MapWrapper v) throws Exception {
		// TODO
		throw new OperationNotSupportedException();
	}

	// Return a XML-safe attribute.  Might want to add camel case support
	private String getCleanLabel(String attributeLabel) {
		attributeLabel = attributeLabel.replaceAll("[()]", "").replaceAll("[^\\w\\s]", "_").replaceAll(" ", "_");
		return attributeLabel;
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MapWrapper {
		@XmlAnyElement
		List<Object> elements;
	}

}
