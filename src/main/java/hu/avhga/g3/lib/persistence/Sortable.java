package hu.avhga.g3.lib.persistence;

import java.util.List;
import java.util.Map;

public interface Sortable {
	Map<String, String> getSortMap();

	List<String> getSort();
}
