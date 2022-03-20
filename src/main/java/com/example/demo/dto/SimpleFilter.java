package com.example.demo.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleFilter {
	private final String name;
	private final Object value;
	private final PredicateType predicate;
	public enum PredicateType {
		LIKE,
		I_LIKE,
		EQUAL,
		IN, NOT_EQUAL
	}
}
