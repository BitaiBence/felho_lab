package hu.avhga.g3.lib.persistence;

import hu.avhga.g3.lib.util.AnnotationUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class SpecificationBuilder<T> {
	protected static final Logger logger = LoggerFactory.getLogger(SpecificationBuilder.class);

	public Specification<T> buildSearchConditions(AbstractSearchCriteria abstractSearchCriteria) {
		BeanWrapper searchSettingsWrapper = new BeanWrapperImpl(abstractSearchCriteria);
		Specification<T> specification = null;
		for ( PropertyDescriptor propertyDescriptor : searchSettingsWrapper.getPropertyDescriptors() ) {
			Search search = AnnotationUtils.getAnnotation(propertyDescriptor, abstractSearchCriteria.getClass(), Search.class);
			Object fieldValue = getPropertyValue(searchSettingsWrapper, propertyDescriptor);
			Specification<T> currentSpecification = buildCondition(search, fieldValue);
			if ( currentSpecification != null ) {
				specification = buildSpecification(search, currentSpecification, specification);
			}
		}
		Specification<T> customSpecification = abstractSearchCriteria.getCustomSpecification();
		if ( customSpecification != null ) {
			if ( specification == null ) {
				specification = where(customSpecification);
			} else {
				specification = specification.and(customSpecification);
			}
		}

		return specification;
	}

	protected Specification<T> buildSpecification(Search search, Specification<T> currentSpecification, Specification<T> allSpecification) {
		if ( allSpecification == null ) {
			return buildFirstSpecification(search, currentSpecification);
		} else {
			return buildAdditionalAndSpecification(search, currentSpecification, allSpecification);
		}
	}

	protected Specification<T> buildFirstSpecification(Search search, Specification<T> currentSpecification) {
		if ( search.orNull() ) {
			return where(currentSpecification.or(builOrNulldCondition(search)));
		} else {
			return where(currentSpecification);
		}
	}

	protected Specification<T> buildAdditionalAndSpecification(Search search, Specification<T> currentSpecification, Specification<T> allSpecification) {
		if ( search.orNull() ) {
			return allSpecification.and(currentSpecification.or(builOrNulldCondition(search)));
		} else {
			return allSpecification.and(currentSpecification);
		}
	}

	protected Specification<T> buildAdditionalOrSpecification(Search search, Specification<T> currentSpecification, Specification<T> allSpecification) {
		if ( search.orNull() ) {
			return allSpecification.or(currentSpecification.or(builOrNulldCondition(search)));
		} else {
			return allSpecification.or(currentSpecification);
		}
	}

	protected Object getPropertyValue(BeanWrapper bw, PropertyDescriptor propertyDescriptor) {
		try {
			return bw.getPropertyValue(propertyDescriptor.getName());
		} catch (Exception e) {
			return null;
		}
	}

	protected Specification<T> builOrNulldCondition(Search search) {
		return (root, query, criteriaBuilder) ->
				criteriaBuilder.isNull(getPath(root, search.path(), search.embedded()));
	}

	protected Specification<T> buildCondition(Search search, Object fieldValue) {
		if ( search != null && fieldValue != null ) {
			return switch (search.type()) {
				case EQUALS -> (root, query, criteriaBuilder) ->
						criteriaBuilder.equal(getPath(root, search.path(), search.embedded()), fieldValue);
				case NOT_EQUALS -> (root, query, criteriaBuilder) ->
						criteriaBuilder.notEqual(getPath(root, search.path(), search.embedded()), fieldValue);
				case IS_NULL -> (root, query, criteriaBuilder) ->
						Boolean.TRUE.equals(fieldValue) ? criteriaBuilder.isNull(getPath(root, search.path(), search.embedded())) :
								criteriaBuilder.isNotNull(getPath(root, search.path(), search.embedded()));
				case IS_NOT_NULL -> (root, query, criteriaBuilder) ->
						Boolean.TRUE.equals(fieldValue) ? criteriaBuilder.isNotNull(getPath(root, search.path(), search.embedded())) :
								criteriaBuilder.isNull(getPath(root, search.path(), search.embedded()));
				case GREATER -> (root, query, criteriaBuilder) ->
						criteriaBuilder.gt(getPath(root, search.path(), search.embedded()), (Number) fieldValue);
				case GREATER_EQUALS -> (root, query, criteriaBuilder) ->
						criteriaBuilder.ge(getPath(root, search.path(), search.embedded()), (Number) fieldValue);
				case LESS -> (root, query, criteriaBuilder) ->
						criteriaBuilder.lt(getPath(root, search.path(), search.embedded()), (Number) fieldValue);
				case LESS_EQUALS -> (root, query, criteriaBuilder) ->
						criteriaBuilder.le(getPath(root, search.path(), search.embedded()), (Number) fieldValue);
				case LIKE -> (root, query, criteriaBuilder) ->
						criteriaBuilder.like(getPath(root, search.path(), search.embedded()), "%" + fieldValue + "%");
				case NOT_LIKE -> (root, query, criteriaBuilder) ->
						criteriaBuilder.notLike(getPath(root, search.path(), search.embedded()), "%" + fieldValue + "%");
				case IN -> (root, query, criteriaBuilder) ->
						getPath(root, search.path(), search.embedded()).in(createArray(fieldValue));
				case NOT_IN -> (root, query, criteriaBuilder) ->
						criteriaBuilder.not(getPath(root, search.path(), search.embedded()).in(createArray(fieldValue)));
				case CLASS_EQUALS -> (root, query, criteriaBuilder) ->
						criteriaBuilder.equal(root.type(), criteriaBuilder.literal(fieldValue));
				case DATE_TO -> (root, query, criteriaBuilder) ->
						criteriaBuilder.lessThanOrEqualTo(getPath(root, search.path(), search.embedded()).as(Date.class), convertToDate(fieldValue));
				case DATE_TIME_TO -> (root, query, criteriaBuilder) ->
						criteriaBuilder.lessThanOrEqualTo(getPath(root, search.path(), search.embedded()).as(Date.class),
								convertToDateCorrectedForEndOfTheDay(fieldValue));
				case DATE_FROM -> (root, query, criteriaBuilder) ->
						criteriaBuilder.greaterThanOrEqualTo(getPath(root, search.path(), search.embedded()).as(Date.class), convertToDate(fieldValue));
				case STARTS_WITH -> (root, query, criteriaBuilder) ->
						criteriaBuilder.like(getPath(root, search.path(), search.embedded()), fieldValue + "%");
				case NOT_STARTS_WITH -> (root, query, criteriaBuilder) ->
						criteriaBuilder.notLike(getPath(root, search.path(), search.embedded()), fieldValue + "%");
				case ENDS_WITH -> (root, query, criteriaBuilder) ->
						criteriaBuilder.like(getPath(root, search.path(), search.embedded()), "%" + fieldValue);
				case NOT_ENDS_WITH -> (root, query, criteriaBuilder) ->
						criteriaBuilder.notLike(getPath(root, search.path(), search.embedded()), "%" + fieldValue);
				case BOOLEAN_TRUE_EQUALS -> (root, query, criteriaBuilder) ->
						Boolean.TRUE.equals(fieldValue) ?
								criteriaBuilder.equal(getPath(root, search.path(), search.embedded()), fieldValue) :
								criteriaBuilder.or(criteriaBuilder.equal(getPath(root, search.path(), search.embedded()), fieldValue),
										criteriaBuilder.isNull(getPath(root, search.path(), search.embedded())));
				case BOOLEAN_FALSE_EQUALS -> (root, query, criteriaBuilder) ->
						Boolean.FALSE.equals(fieldValue) ?
								criteriaBuilder.equal(getPath(root, search.path(), search.embedded()), fieldValue) :
								criteriaBuilder.or(criteriaBuilder.equal(getPath(root, search.path(), search.embedded()), fieldValue),
										criteriaBuilder.isNull(getPath(root, search.path(), search.embedded())));

				case IS_EMPTY -> (root, query, criteriaBuilder) ->
						Boolean.TRUE.equals(fieldValue) ? criteriaBuilder.isEmpty(getPath(root, search.path(), search.embedded())) :
								criteriaBuilder.isNotEmpty(getPath(root, search.path(), search.embedded()));
				case IS_NOT_EMPTY -> (root, query, criteriaBuilder) ->
						Boolean.TRUE.equals(fieldValue) ? criteriaBuilder.isNotEmpty(getPath(root, search.path(), search.embedded())) :
								criteriaBuilder.isEmpty(getPath(root, search.path(), search.embedded()));
			};
		}
		return null;
	}

	private Object[] createArray(Object fieldValue) {
		Object[] array;
		if ( fieldValue instanceof List ) {
			array = ((List<?>) fieldValue).toArray();
		} else if ( fieldValue instanceof Collection ) {
			array = new ArrayList<Object>((Collection<?>) fieldValue).toArray();
		} else if ( fieldValue.getClass().isArray() ) {
			array = (Object[]) fieldValue;
		} else {
			array = new Object[] { fieldValue };
		}
		return array;
	}

	public static <P> Path<P> getPath(Root<?> root, String path, boolean embedded) {
		if ( path.contains(".") ) {
			return getPathFromComplexValue(root, path, embedded);
		} else {
			return root.get(path);
		}
	}

	public static <P> Path<P> getPathFromComplexValue(Root<?> root, String path, boolean embedded) {
		Path<P> returnPath = null;
		String[] fragments = path.split("[.]");

		if ( embedded ) {
			returnPath = root.get(fragments[0]);
			for ( int i = 1; i < fragments.length; i++ ) {
				returnPath = returnPath.get(fragments[i]);
			}
		} else {
			Join<Object, Object> joins = root.join(fragments[0], JoinType.LEFT);
			for ( int i = 1; i < fragments.length; i++ ) {
				if ( i != (fragments.length - 1) ) {
					joins = joins.join(fragments[i], JoinType.LEFT);
				} else {
					returnPath = joins.get(fragments[i]);
				}
			}

		}
		return returnPath;
	}

	public static <P> Map<String, Path<P>> getPathsFromSameObject(Root<?> root, String mainPath, List<String> paths) {
		Map<String, Path<P>> pathMap = new HashMap<>();

		if ( mainPath.contains(".") ) {
			String[] fragments = mainPath.split("[.]");
			Join<Object, Object> joins = root.join(fragments[0], JoinType.LEFT);
			for ( int i = 1; i < fragments.length; i++ ) {
				if ( i != (fragments.length - 1) ) {
					joins = joins.join(fragments[i], JoinType.LEFT);
				} else {
					for ( String path : paths ) {
						pathMap.put(path, joins.get(path));
					}
				}

			}
		} else {
			Join<Object, Object> joins = root.join(mainPath, JoinType.LEFT);
			for ( String path : paths ) {
				pathMap.put(path, joins.get(path));
			}
		}
		return pathMap;
	}

	private Date convertToDate(Object value) {
		if ( value instanceof LocalDate date ) {
			return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else if ( value instanceof LocalDateTime dateTime ) {
			return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}

	private Date convertToDateCorrectedForEndOfTheDay(Object value) {
		if ( value instanceof LocalDate date ) {
			return Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else if ( value instanceof LocalDateTime dateTime ) {
			return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}
}