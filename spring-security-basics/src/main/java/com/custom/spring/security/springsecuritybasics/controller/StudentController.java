package com.custom.spring.security.springsecuritybasics.controller;

import com.custom.spring.security.springsecuritybasics.model.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/students")
public class StudentController {

    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "First Student"),
            new Student(2, "Second Student"),
            new Student(3, "Third Student")

    );

    @GetMapping(path = "{studentId}")
    public Student getStudents(@PathVariable("studentId") Integer studentId) {

        return STUDENTS.stream().filter(eachStudent -> studentId.equals(eachStudent.getStudentId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Student " + studentId + "does not exists"));

    }

    ;

}
