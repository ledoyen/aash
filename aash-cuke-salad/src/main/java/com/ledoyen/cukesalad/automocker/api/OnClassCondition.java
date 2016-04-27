package com.ledoyen.cukesalad.automocker.api;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * Copied from {@code org.springframework.boot.autoconfigure.condition.OnClassCondition}.
 */
class OnClassCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		StringBuffer matchMessage = new StringBuffer();

		MultiValueMap<String, Object> onClasses = getAttributes(metadata, ConditionalOnClass.class);
		if (onClasses != null) {
			List<String> missing = getMatchingClasses(onClasses, MatchType.MISSING, context);
			if (!missing.isEmpty()) {
				return false;
			}
			matchMessage.append("@ConditionalOnClass classes found: " + StringUtils
					.collectionToCommaDelimitedString(getMatchingClasses(onClasses, MatchType.PRESENT, context)));
		}

		MultiValueMap<String, Object> onMissingClasses = getAttributes(metadata, ConditionalOnMissingClass.class);
		if (onMissingClasses != null) {
			List<String> present = getMatchingClasses(onMissingClasses, MatchType.PRESENT, context);
			if (!present.isEmpty()) {
				return false;
			}
			matchMessage.append(matchMessage.length() == 0 ? "" : " ");
			matchMessage
					.append("@ConditionalOnMissing classes not found: " + StringUtils.collectionToCommaDelimitedString(
							getMatchingClasses(onMissingClasses, MatchType.MISSING, context)));
		}

		return true;
	}

	private MultiValueMap<String, Object> getAttributes(AnnotatedTypeMetadata metadata, Class<?> annotationType) {
		return metadata.getAllAnnotationAttributes(annotationType.getName(), true);
	}

	private List<String> getMatchingClasses(MultiValueMap<String, Object> attributes, MatchType matchType,
			ConditionContext context) {
		List<String> matches = new LinkedList<String>();
		addAll(matches, attributes.get("value"));
		addAll(matches, attributes.get("name"));
		Iterator<String> iterator = matches.iterator();
		while (iterator.hasNext()) {
			if (!matchType.matches(iterator.next(), context)) {
				iterator.remove();
			}
		}
		return matches;
	}

	private void addAll(List<String> list, List<Object> itemsToAdd) {
		if (itemsToAdd != null) {
			for (Object item : itemsToAdd) {
				Collections.addAll(list, (String[]) item);
			}
		}
	}

	private enum MatchType {

		PRESENT {
			@Override
			public boolean matches(String className, ConditionContext context) {
				return ClassUtils.isPresent(className, context.getClassLoader());
			}
		},

		MISSING {
			@Override
			public boolean matches(String className, ConditionContext context) {
				return !ClassUtils.isPresent(className, context.getClassLoader());
			}
		};

		public abstract boolean matches(String className, ConditionContext context);

	}

}
