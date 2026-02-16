package hu.avhga.g3.lib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationUtils {
	public static final Logger logger = LoggerFactory.getLogger(AnnotationUtils.class);

	private AnnotationUtils() {
	}

	public static <T extends Annotation> T getAnnotation(PropertyDescriptor propertyDescriptor, Class<?> clazz, Class<T> annotationClass) {
		T annotation;
		try {
			Field field = findField(clazz, propertyDescriptor.getName());
			annotation = field.getAnnotation(annotationClass);
			if ( annotation != null ) {
				return annotation;
			}
		} catch (SecurityException e) {
			logger.error("Nem lekérdezhető az annotáció", e);
		} catch (NoSuchFieldException e) {
			// Nem probléma
		}
		Method read = propertyDescriptor.getReadMethod();
		if ( read != null ) {
			annotation = read.getAnnotation(annotationClass);
			if ( annotation != null ) {
				return annotation;
			}
		}
		Method write = propertyDescriptor.getWriteMethod();
		if ( write != null ) {
			annotation = write.getAnnotation(annotationClass);
			return annotation;
		}
		return null;
	}

	private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException();
	}
}
