# Analyses

The reconfirmed problem is that when you search for the `document.name` with `UPPER(document.name)` the system takes up
to 4min. This simple application tries to reproduce the code structure in production, use H2 with file storage as I
don't have an oracle 19c db.

If the `db.populate`property is set to true then will insert `db.how-many-records` in DOCUMENTS and ACTIVITIES.

In production `V_LOGS` is mapped as `@Table`

## DB SCHEMA

~~~oracle
-- DOCUMENTS
CREATE TABLE DOCUMENTS
(
    ID       NUMBER(16) PRIMARY KEY,
    NAME     VARCHAR(1024) NOT NULL,
    DOC_SIZE NUMBER(16)    NOT NULL,
    CREATED  TIMESTAMP     NOT NULL
    --others, removed for brevity
);
CREATE INDEX IDX_DOC_NAME_UPPER ON DOCUMENTS (UPPER(NAME));

-- USERS
CREATE TABLE USERS
(
    ID    NUMBER(16) PRIMARY KEY,
    CNAME VARCHAR(256) NOT NULL
    --others, removed for brevity
);

-- Activities
CREATE TABLE ACTIVITIES
(
    ID     NUMBER(16) PRIMARY KEY,
    USR_ID NUMBER(16) references USERS (ID) NOT NULL,
    DOC_ID NUMBER(16) references DOCUMENTS (ID),
    PRT_ID NUMBER(16) references USERS (ID) NOT NULL
    --others, removed for brevity
);
CREATE INDEX IDX_ACT_USR_ID ON ACTIVITIES (USR_ID);
CREATE INDEX IDX_ACT_DOC_ID ON ACTIVITIES (DOC_ID);

-- LOGS
CREATE VIEW V_LOGS AS
SELECT CNAME,
       ACTIVITIES.USR_ID,
       PRT_ID,
       DOC_ID
       --others, removed for brevity
FROM ACTIVITIES,
     USERS
WHERE USERS.ID = ACTIVITIES.USR_ID;
~~~

## ENTITIES

~~~java

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

@Entity
@Getter
@Immutable
@Table(name = "V_LOGS")
public class LogsView {

	@Id
	@Column(name = "USR_ID", nullable = false)
	private long usrId;

	@Column(name = "PRT_ID", nullable = false)
	private long prtId;

	@Column(name = "DOC_ID")
	private Long docId;

	@ManyToOne
	@JoinColumn(name = "USR_ID",
			nullable = false,
			insertable = false,
			updatable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "PRT_ID",
			nullable = false,
			insertable = false,
			updatable = false)
	private User partner;

	@ManyToOne
	@JoinColumn(name = "DOC_ID",
			insertable = false,
			updatable = false)
	private Document document;

	//others, removed for brevity
}


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
	
	//others, removed for brevity
}
~~~

## SPECIFICATION

~~~java

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
					builder.upper(PredicateUtils.getChildPath(root, keys)),
					("%" + filter.getValue() + "%").toUpperCase());
		} else {
			predicate = builder.equal(
					PredicateUtils.getChildPath(root, keys),
					filter.getValue());
		}

		return predicate;
	}
}
~~~

### REPOSITORY

~~~java

@Repository
public interface LogsRepository extends ReadOnlyRepository<LogsView, Long>, JpaSpecificationExecutor<Logs> {
}
~~~

### SERVICE

~~~java

@Service
@RequiredArgsConstructor
public class LogsService {
	private final LogsRepository logsRepository;
	private final ObjectMapper mapper;
	private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

	public Page<LogsDto> paginated(String name, String value) {
		var type = isNumeric(value) ? SimpleFilter.PredicateType.EQUAL : SimpleFilter.PredicateType.LIKE;
		var filter = new SimpleFilter(name, value, type);
		return logsRepository
				.findAll(new LogsSpecification(List.of(filter)), Pageable.ofSize(10))
				.map(en -> mapper.convertValue(en, LogsDto.class));
	}


	private boolean isNumeric(String strNum) {
		return strNum != null && pattern.matcher(strNum).matches();
	}
}

~~~

## PRODUCTION

The DOCUMENTS table contains approximately 2M records The ACTIVITIES table contains approximately 7M records.

### Db

Oracle 19c, on another server.

### QUERY GENERATED

~~~sql
-- Hibernate: 
-- executing 5 JDBC statements;
select *
from (select * -- removed for brevity
      from v_logs v_logs0_
               cross join documents document1_
      where v_logs0_.doc_id = document1_.id
        and (
          upper(document1_.name) like ?
          )
        and (
              v_logs0_.usr_id in (4, 72, 76, 123, 147, 199, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)
          )
        and v_logs0_.usr_id <> 1
      order by v_logs0_.usr_id asc)
where rownum <= ?
~~~

#### Without document.name filter, page 10
    1.228738106 seconds
#### With document.name filter, page 10 and results
    2.900642325 seconds
#### With document.name filter, page 10 and no results
    240.123813697 seconds
    sqlpus: 2-3 seconds


### SQL FIDDLE
#### [View Execution Plan][1]

## CONFIGURATION
- **Database**: Oracle 19c
- **Jdk**: 11
- **Ojdbc8**: 21.5.0
- **SpringBoot**: 2.6.4
- **Hibernate**: 5.6.5.Final
