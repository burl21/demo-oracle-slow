package com.example.demo;

import com.example.demo.dto.LogsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/logs/")
public class LogsController {
	private final LogsService logsService;

	@GetMapping("{name}/{value}")
	public List<LogsDto> logs(@PathVariable String name,
	                          @PathVariable String value) {
		return logsService.paginated(name, value).getContent();
	}
}
