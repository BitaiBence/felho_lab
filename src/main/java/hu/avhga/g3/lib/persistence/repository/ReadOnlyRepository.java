package hu.avhga.g3.lib.persistence.repository;

import hu.avhga.g3.lib.persistence.repository.AbstractRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ReadOnlyRepository<T> extends AbstractRepository<T> {

	@Override
	default <S extends T> S save(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	default <S extends T> S saveAndFlush(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	default <S extends T> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	default <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	default long delete(Specification<T> spec) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void delete(T entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteAll(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteAllByIdInBatch(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteAllInBatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteAllInBatch(Iterable<T> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void deleteInBatch(Iterable<T> entities) {
		throw new UnsupportedOperationException();
	}
}