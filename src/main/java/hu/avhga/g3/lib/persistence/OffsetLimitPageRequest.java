package hu.avhga.g3.lib.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class OffsetLimitPageRequest implements Pageable, Serializable {

	@Serial
	private static final long serialVersionUID = -25822477129613575L;

	private final int limit;
	private final long offset;
	private final Sort sort;

	/**
	 * Creates a new {@link OffsetLimitPageRequest} with sort parameters applied.
	 *
	 * @param offset zero-based offset.
	 * @param limit  the size of the elements to be returned.
	 * @param sort   can be {@literal null}.
	 */
	public OffsetLimitPageRequest(long offset, int limit, Sort sort) {
		if ( offset < 0 ) {
			throw new IllegalArgumentException("Offset index must not be less than zero!");
		}

		if ( limit < 0 ) {
			throw new IllegalArgumentException("Limit must not be less than zero!");
		}
		this.limit = limit;
		this.offset = offset;
		this.sort = sort;
	}

	/**
	 * Creates a new {@link OffsetLimitPageRequest} with sort parameters applied.
	 *
	 * @param offset     zero-based offset.
	 * @param limit      the size of the elements to be returned.
	 * @param direction  the direction of the {@link Sort} to be specified, can be {@literal null}.
	 * @param properties the properties to sort by, must not be {@literal null} or empty.
	 */
	public OffsetLimitPageRequest(long offset, int limit, Sort.Direction direction, String... properties) {
		this(offset, limit, Sort.by(direction, properties));
	}

	/**
	 * Creates a new {@link OffsetLimitPageRequest} with sort parameters applied.
	 *
	 * @param offset zero-based offset.
	 * @param limit  the size of the elements to be returned.
	 */
	public OffsetLimitPageRequest(int offset, int limit) {
		this(offset, limit, Sort.unsorted());
	}

	@Override
	public boolean isPaged() {
		return Pageable.super.isPaged();
	}

	@Override
	public boolean isUnpaged() {
		return Pageable.super.isUnpaged();
	}

	@Override
	public int getPageNumber() {
		return Math.toIntExact(offset / limit);
	}

	@Override
	public int getPageSize() {
		return limit;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Sort getSortOr(Sort sort) {
		return Pageable.super.getSortOr(sort);
	}

	@Override
	public Pageable next() {
		return new OffsetLimitPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
	}

	public OffsetLimitPageRequest previous() {
		return hasPrevious() ? new OffsetLimitPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
	}

	@Override
	public Pageable previousOrFirst() {
		return hasPrevious() ? previous() : first();
	}

	@Override
	public Pageable first() {
		return new OffsetLimitPageRequest(0, getPageSize(), getSort());
	}

	@Override
	public Pageable withPage(int pageNumber) {
		return new OffsetLimitPageRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
	}

	@Override
	public boolean hasPrevious() {
		return offset > limit;
	}

	@Override
	public Optional<Pageable> toOptional() {
		return Pageable.super.toOptional();
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;
		OffsetLimitPageRequest that = (OffsetLimitPageRequest) o;
		return limit == that.limit && offset == that.offset && Objects.equals(sort, that.sort);
	}

	@Override
	public int hashCode() {
		return Objects.hash(limit, offset, sort);
	}

	@Override
	public String toString() {
		return "OffsetLimitPageRequest{" +
				"limit=" + limit +
				", offset=" + offset +
				", sort=" + sort +
				'}';
	}
}