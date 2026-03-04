package com.spring.ai.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table (name = "student")
@Data
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private Integer age;
    private String email;
}
