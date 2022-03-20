package com.example.demo;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

final class PredicateUtils {
	private PredicateUtils() {
	}

	public static <T, S> Path<T> getChildPath(Root<S> root, String[] keys) {
		// 'x.y' => root.get("x").get("y")
		Path<T> path = null;
		for (var key : keys) {
			path = path == null ? root.get(key) : path.get(key);
		}

		return path;
	}
}
