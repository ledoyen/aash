package com.ledoyen.tool;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class Lists2 {

	public static <T> List<T> flatten(Collection<?> list) {
		List<T> retVal = Lists.newArrayList();
		flatten(list, retVal);
		return retVal;
	}
 
	@SuppressWarnings("unchecked")
	private static <T> void flatten(Collection<?> fromTreeList, List<T> toFlatList) {
		for (Object item : fromTreeList) {
			if (item instanceof Collection<?>) {
				flatten((Collection<?>) item, toFlatList);
			} else {
				toFlatList.add((T) item);
			}
		}
	}
}
