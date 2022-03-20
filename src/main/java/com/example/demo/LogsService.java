package com.example.demo;

import com.example.demo.domain.LogsRepository;
import com.example.demo.dto.LogsDto;
import com.example.demo.dto.SimpleFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LogsService {
	private final LogsRepository logsRepository;
	private final ObjectMapper mapper;
	private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public Page<LogsDto> paginated(String name, String value) {
		var type = isNumeric(value) ? SimpleFilter.PredicateType.EQUAL : SimpleFilter.PredicateType.LIKE;
		// only records with users in my groups
		var users = List.of(1L, 2L, 4L, 10L, 14L, 15L, 7L, 9L);
		var filters = new ArrayList<SimpleFilter>();
		filters.add(new SimpleFilter(name, value, type));
		filters.add(new SimpleFilter("usrId", users, SimpleFilter.PredicateType.IN));

		return logsRepository
				.findAll(new LogsSpecification(filters), Pageable.ofSize(10))
				.map(en -> mapper.convertValue(en, LogsDto.class));
	}


	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}
}
