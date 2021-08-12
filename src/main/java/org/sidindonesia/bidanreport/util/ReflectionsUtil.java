package org.sidindonesia.bidanreport.util;

import java.util.Set;

import javax.persistence.Entity;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionsUtil {

	public static Set<Class<?>> getAllEntityClasses() {
		Reflections reflections = new Reflections("org.sidindonesia.bidanreport.domain", new TypeAnnotationsScanner(),
			new SubTypesScanner());
		return reflections.getTypesAnnotatedWith(Entity.class);
	}
}
