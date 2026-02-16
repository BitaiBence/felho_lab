package hu.avhga.g3.lib.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class PageableBuilder {
	public static final Logger logger = LoggerFactory.getLogger(PageableBuilder.class);

	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_LIMIT = 100;

	public Pageable createPageable() {
		return new OffsetLimitPageRequest(DEFAULT_OFFSET, DEFAULT_LIMIT, Sort.by(getDefaultOrders()));
	}

	public Pageable createPageable(PageableSortableSearchCriteria criteria) {
		if ( criteria != null ) {
			if ( criteria.getPagination() != null ) {
				return createPageable(criteria.getPagination().getOffset(), criteria.getPagination().getLimit(), criteria.getSort(), criteria.getSortMap());
			}
			return createPageable(criteria.getSort(), criteria.getSortMap());
		}
		return createPageable();
	}

	public Pageable createPageable(List<String> sort, Map<String, String> sortMap) {
		List<Sort.Order> orders = getOrders(sort, sortMap);
		return new OffsetLimitPageRequest(DEFAULT_OFFSET, DEFAULT_LIMIT, Sort.by(orders));
	}

	public Pageable createPageable(Integer offsetParam, Integer limitParam, List<String> sort, Map<String, String> sortMap) {
		int offset = DEFAULT_OFFSET;
		int limit = DEFAULT_LIMIT;

		if ( offsetParam != null ) {
			offset = offsetParam;
		}
		if ( limitParam != null ) {
			limit = limitParam;
		}

		List<Sort.Order> orders = getOrders(sort, sortMap);
		return new OffsetLimitPageRequest(offset, limit, Sort.by(orders));
	}

	protected List<Sort.Order> getDefaultOrders() {
		List<Sort.Order> orders = new ArrayList<>();
		Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id", Sort.NullHandling.NULLS_FIRST);
		orders.add(order);
		return orders;
	}

	private List<Sort.Order> getOrders(List<String> sort, Map<String, String> sortMap) {
		List<Sort.Order> orders = new ArrayList<>();

		if ( sort != null && !sort.isEmpty() ) {
			orders = sort.stream()
					.map(s -> mapToOrder(s, sortMap))
					.filter(Objects::nonNull)
					.filter(distinctByKey(Sort.Order::getProperty))
					.toList();
		}
		if ( orders.isEmpty() ) {
			orders = getDefaultOrders();
		}
		return orders;
	}

	private Sort.Order mapToOrder(String s, Map<String, String> sortMap) {
		if ( s.trim().isEmpty() ) {
			return null;
		}
		boolean isDesc = s.charAt(0) == '-';
		Sort.Direction direction;
		Sort.NullHandling nullHandling;
		if ( !isDesc ) {
			direction = Sort.Direction.ASC;
			nullHandling = Sort.NullHandling.NULLS_FIRST;
		} else {
			direction = Sort.Direction.DESC;
			nullHandling = Sort.NullHandling.NULLS_LAST;
		}
		String sortBy = s;
		if ( isDesc || s.startsWith("+") ) {
			sortBy = s.substring(1);
		}
		if ( sortMap != null &&
				sortMap.containsKey(sortBy) ) {
			sortBy = sortMap.get(sortBy);
		}
		if ( !sortBy.trim().isEmpty() ) {
			return new Sort.Order(direction, sortBy, nullHandling).ignoreCase();
		} else {
			logger.warn("Nem található rendezési feltétel mező: {}", sortBy);
			return null;
		}
	}

	public <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}
}
