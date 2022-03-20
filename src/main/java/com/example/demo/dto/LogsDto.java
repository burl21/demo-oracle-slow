package com.example.demo.dto;

import com.example.demo.domain.Document;
import com.example.demo.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogsDto {
	private long usrId;
	private long prtId;
	private Long docId;
	private User user;
	private User partner;
	private Document document;
}
