package com.example.demo.domain;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "USERS")
@SequenceGenerator(allocationSize = 1, name = "USERS_ID", sequenceName = "USERS_ID")
public class User {
	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_ID")
	private Long id;

	@NotNull
	@Column(name = "CNAME", nullable = false, length = 256)
	private String cname;
}
