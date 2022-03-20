package com.example.demo;

import com.example.demo.domain.LogsView;
import com.example.demo.dto.SimpleFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.example.demo.dto.SimpleFilter.PredicateType.IN;
import static com.example.demo.dto.SimpleFilter.PredicateType.LIKE;

@RequiredArgsConstructor
public class LogsSpecification implements Specification<LogsView> {
	private final List<SimpleFilter> filters;

	@Override
	public Predicate toPredicate(Root<LogsView> root,
	                             CriteriaQuery<?> criteriaQuery,
	                             CriteriaBuilder criteriaBuilder) {
		if (CollectionUtils.isEmpty(filters)) return null;

		Predicate[] predicates = filters.stream()
				.filter(Objects::nonNull)
				.map(filter -> getPredicate(filter, root, criteriaBuilder))
				.toArray(Predicate[]::new);

		return criteriaBuilder.and(predicates);
	}

	private Predicate getPredicate(SimpleFilter filter,
	                               Root<LogsView> root,
	                               CriteriaBuilder builder) {
		Objects.requireNonNull(filter.getName());
		Predicate predicate;
		String[] keys = filter.getName().split("\\.");
		if (filter.getPredicate() == LIKE) {
			predicate = builder.like(
					builder.lower(PredicateUtils.getChildPath(root, keys)),
					("%" + filter.getValue() + "%").toLowerCase());
		} else if (filter.getPredicate() == IN) {
			predicate = PredicateUtils.getChildPath(root, keys).in((Collection<?>) filter.getValue());
		} else {
			predicate = builder.equal(
					PredicateUtils.getChildPath(root, keys),
					filter.getValue());
		}

		return predicate;
	}

}
