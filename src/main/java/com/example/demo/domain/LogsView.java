package com.example.demo.domain;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;

@Entity
@Getter
@Immutable
@Subselect("V_LOGS")
public class LogsView {

	@Id
	@Column(name = "USR_ID", nullable = false)
	private long usrId;

	@Column(name = "PRT_ID", nullable = false)
	private long prtId;

	@Column(name = "DOC_ID")
	private Long docId;

	@ManyToOne
	@JoinColumn(
			name = "USR_ID",
			nullable = false,
			insertable = false,
			updatable = false)
	private User user;

	@ManyToOne
	@JoinColumn(
			name = "PRT_ID",
			nullable = false,
			insertable = false,
			updatable = false)
	private User partner;

	@ManyToOne
	@JoinColumn(
			name = "DOC_ID",
			insertable = false,
			updatable = false)
	private Document document;
}
