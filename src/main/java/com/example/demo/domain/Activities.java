package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "ACTIVITIES",
		indexes = {
				@Index(columnList = "USR_ID", name = "IDX_ACTIVITIES_ID"),
				@Index(columnList = "PRT_ID", name = "IDX_ACTIVITIES_PRT_ID"),
				@Index(columnList = "DOC_ID", name = "IDX_ACTIVITIES_DOC_ID")
		}
)
@SequenceGenerator(allocationSize = 1, name = "ACTIVITIES_ID", sequenceName = "ACTIVITIES_ID")
public class Activities {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITIES_ID")
	private Long id;

	@Column(name = "USR_ID", nullable = false)
	private long usrId;

	@Column(name = "PRT_ID", nullable = false)
	private long prtId;

	@Column(name = "DOC_ID")
	private Long docId;
}
