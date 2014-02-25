package com.ledoyen.tool;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public final class Multimaps2 {

	private Multimaps2() {}

	public static <K,V1,V2> Map<K,V2> toMap(Multimap<K, V1> multimap, Function<Collection<V1>, V2> flatFunction) {
		Map<K,V2> map = Maps.newHashMap();
		for(Entry<K, Collection<V1>> entry : multimap.asMap().entrySet()) {
			map.put(entry.getKey(), flatFunction.apply(entry.getValue()));
		}
		return map;
	}
}
