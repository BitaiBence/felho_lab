package hu.avhga.g3.lib.persistence;

import org.springframework.data.jpa.domain.Specification;

public interface AbstractSearchCriteria {
	default <T> Specification<T> getCustomSpecification() {
		return null;
	}
}
