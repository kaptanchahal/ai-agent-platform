package com.spring.ai.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tool_audit")
@Data
public class ToolAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String toolName;

    @Column(length = 2000)
    private String arguments;

    @Column(length = 2000)
    private String result;

    private LocalDateTime executedAt;
}
