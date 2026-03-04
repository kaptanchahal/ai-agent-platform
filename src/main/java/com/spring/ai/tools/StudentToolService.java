package com.spring.ai.tools;

import com.spring.ai.entity.Student;
import com.spring.ai.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentToolService {

    private final StudentRepository studentRepository;

    private static final Logger log = LoggerFactory.getLogger(StudentToolService.class);

    @Tool(
            name = "createStudent",
            description = "Create a new student in the database. Use this tool whenever the user wants to create, add, or onboard a student."
    )
    public String createStudent(
            @ToolParam(description = "Full name of the student") String name,
            @ToolParam(description = "Age of the student in years") Integer age,
            @ToolParam(description = "Email address of the student") String email) {
        try {

            Student student = new Student();
            student.setName(name);
            student.setAge(age);
            student.setEmail(email);
            studentRepository.save(student);

            log.info("Student created successfully: {} with id: {}", name, student.getId());

            return "Student created successfully with id: " + student.getId();
        } catch (Exception e) {

            log.error("Error creating student: {}", name, e);

            return "Error: Failed to create student - " + e.getMessage();
        }
    }
}
