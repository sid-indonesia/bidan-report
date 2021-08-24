package org.sidindonesia.bidanreport.util;

import static java.util.stream.Collectors.toCollection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.stereotype.Repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionsUtil {
	static final TypeAnnotationsScanner TYPE_ANNOTATIONS_SCANNER = new TypeAnnotationsScanner();
	static final SubTypesScanner SUB_TYPES_SCANNER = new SubTypesScanner();

	private static final Set<Class<?>> REPOSITORY_CLASSES = new Reflections("org.sidindonesia.bidanreport.repository",
		TYPE_ANNOTATIONS_SCANNER, SUB_TYPES_SCANNER).getTypesAnnotatedWith(Repository.class);

	private static final Set<Class<?>> ENTITY_CLASSES = new Reflections("org.sidindonesia.bidanreport.domain",
		TYPE_ANNOTATIONS_SCANNER, SUB_TYPES_SCANNER).getTypesAnnotatedWith(Entity.class).stream()
			.collect(toCollection(LinkedHashSet::new));

	public static Set<Class<?>> getAllEntityClasses() {
		return ENTITY_CLASSES;
	}

	public static Class<?> getRepositoryClassOfEntity(Class<?> entityClass) {
		return REPOSITORY_CLASSES.stream().filter(repositoryClass -> {
			Type type = ((ParameterizedType) repositoryClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
			return entityClass.getTypeName().equals(type.getTypeName());
		}).findAny().orElseThrow();
	}
}
