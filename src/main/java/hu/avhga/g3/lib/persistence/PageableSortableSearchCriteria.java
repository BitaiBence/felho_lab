package hu.avhga.g3.lib.persistence;

import hu.avhga.g3.lib.persistence.AbstractSearchCriteria;
import hu.avhga.g3.lib.persistence.Pageable;
import hu.avhga.g3.lib.persistence.PaginationDto;
import hu.avhga.g3.lib.persistence.Sortable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PageableSortableSearchCriteria implements AbstractSearchCriteria, Sortable, Pageable {
	private PaginationDto pagination;
	private List<String> sort;

	@Override
	public PaginationDto getPagination() {
		return pagination;
	}

	public void setPagination(PaginationDto pagination) {
		this.pagination = pagination;
	}

	@Override
	public List<String> getSort() {
		return sort;
	}

	public void setSort(List<String> sort) {
		this.sort = sort;
	}

	@Override
	public Map<String, String> getSortMap() {
		return Collections.emptyMap();
	}
}
