package com.innogames.jenkinslib.container

class Config {

	Map<String, Object> map

	Config(Map<String, Object> map) {
		this.map = map
	}

//	@Override
	int size() {
		return this.map.size()
	}

//	@Override
	boolean isEmpty() {
		return this.map.isEmpty()
	}

//	@Override
	boolean containsKey(Object key) {
		return this.map.containsKey(key)
	}

//	@Override
	boolean containsValue(Object value) {
		return this.map.containsValue(value)
	}

//	@Override
	Object get(Object key) {
		return this.map.get(key)
	}

//	@Override
	Object put(String key, Object value) {
		return this.map.put(key, value)
	}

//	@Override
	Object remove(Object key) {
		return this.map.remove(key)
	}

//	@Override
	void putAll(Map<? extends String, ?> m) {
		this.map.putAll(m)
	}

//	@Override
	void clear() {
		this.map.clear()
	}

//	@Override
	Set<String> keySet() {
		return this.map.keySet()
	}

//	@Override
	Collection<Object> values() {
		return this.map.values()
	}

//	@Override
	Set<Map.Entry<String, Object>> entrySet() {
		return this.map.entrySet()
	}

}
