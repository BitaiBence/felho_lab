package hu.avhga.g3.lib.util;

import hu.avhga.g3.lib.exception.ValidationException;
import hu.avhga.g3.lib.util.AnnotationUtils;
import jakarta.persistence.Column;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class EntityValidator {

	private EntityValidator() {
	}

	public static void validate(Object entity) {
		BeanWrapper searchSettingsWrapper = new BeanWrapperImpl(entity);
		for ( PropertyDescriptor propertyDescriptor : searchSettingsWrapper.getPropertyDescriptors() ) {
			if ( propertyDescriptor.getPropertyType() != null ) {
				checkField(searchSettingsWrapper, propertyDescriptor);
			}
		}
	}

	private static void checkField(BeanWrapper searchSettingsWrapper, PropertyDescriptor propertyDescriptor) {
		Object objectFieldValue = getPropertyValue(searchSettingsWrapper, propertyDescriptor);
		Column columnInfo = AnnotationUtils.getAnnotation(propertyDescriptor, searchSettingsWrapper.getWrappedClass(),
				Column.class);
		if ( columnInfo != null ) {
			if ( objectFieldValue instanceof String fieldValue ) {
				validateString(columnInfo, propertyDescriptor, fieldValue);
			} else if ( objectFieldValue instanceof BigDecimal fieldValue ) {
				validateBigDecimal(columnInfo, propertyDescriptor, fieldValue);
			}
		}
	}

	private static void validateString(Column columnInfo, PropertyDescriptor propertyDescriptor, String fieldValue) {
		if ( columnInfo.length() < fieldValue.length() ) {
			throw new ValidationException("A " + propertyDescriptor.getName() + " mező (" + fieldValue
					+ ") túl hosszú. Max hossz:" + columnInfo.length());
		}
	}

	private static void validateBigDecimal(Column columnInfo, PropertyDescriptor propertyDescriptor, BigDecimal fieldValue) {
		BigDecimal noZero = fieldValue.stripTrailingZeros();
		if ( columnInfo.scale() != 0 && columnInfo.precision() != 0 ) {
			if ( noZero.scale() > columnInfo.scale() ) {
				throw new ValidationException("A " + propertyDescriptor.getName() + " mező (" + fieldValue
						+ ") túl sok tizedesjegyet tartalmaz. Max:" + columnInfo.scale());
			}
			noZero = noZero.setScale(columnInfo.scale(), RoundingMode.UNNECESSARY);
			if ( noZero.precision() > columnInfo.precision() ) {
				throw new ValidationException("A " + propertyDescriptor.getName() + " mező (" + fieldValue
						+ ") értéke túl nagy/alacsony. Határ: (+/-)" + (Math.pow(10, (columnInfo.precision() - columnInfo.scale()))));
			}
		}
	}

	private static Object getPropertyValue(BeanWrapper bw, PropertyDescriptor propertyDescriptor) {
		try {
			return bw.getPropertyValue(propertyDescriptor.getName());
		} catch (Exception e) {
			return null;
		}
	}

}
