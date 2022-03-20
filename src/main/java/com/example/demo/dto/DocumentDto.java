package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class DocumentDto {
	private long id;
	private String name;
	private long docSize;
	private Date docDate;
}
