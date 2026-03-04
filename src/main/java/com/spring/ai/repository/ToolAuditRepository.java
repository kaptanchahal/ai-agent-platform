package com.spring.ai.repository;

import com.spring.ai.entity.ToolAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolAuditRepository extends JpaRepository<ToolAudit, String> {
}
