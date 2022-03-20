package com.example.demo.domain;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends ReadOnlyRepository<LogsView, Long>, JpaSpecificationExecutor<LogsView> {
}
