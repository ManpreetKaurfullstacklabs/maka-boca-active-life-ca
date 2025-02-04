package io.reactivestax.activelife.controller;


import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class Dashboard {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;
    @GetMapping("/{id}/waitlisted-courses")
    public ResponseEntity<List<Courses>> getCoursesForWaitlistedMembers(@PathVariable Long id, String isWaitListed) {
        List<Courses> coursesForWaitlistedMembers = familyCourseRegistrationService.getCoursesForWaitlistedMembers();
        return ResponseEntity.ok(coursesForWaitlistedMembers);
    }
}
