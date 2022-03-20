package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DOCUMENTS")
@SequenceGenerator(allocationSize = 1, name = "DOCUMENTS_ID", sequenceName = "DOCUMENTS_ID")
public class Document {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_ID")
	private Long id;

	@Column(name = "NAME", nullable = false, length = 1024)
	private String name;

	@Column(name = "DOC_SIZE", nullable = false)
	private long size;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED", nullable = false)
	private Date created;
}
