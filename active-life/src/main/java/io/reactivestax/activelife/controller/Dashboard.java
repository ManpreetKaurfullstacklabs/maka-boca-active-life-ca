package io.reactivestax.activelife.controller;


import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class Dashboard {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @Autowired
    private OfferredCourseService offerredCourseService;

    @GetMapping("/{id}/waitlisted-courses")
    public ResponseEntity<List<Courses>> getCoursesForWaitlistedMembers(@PathVariable Long id, String isWaitListed) {
        List<Courses> coursesForWaitlistedMembers = familyCourseRegistrationService.getCoursesForWaitlistedMembers();
        return ResponseEntity.ok(coursesForWaitlistedMembers);
    }

    @PostMapping("/search")
    public List<OfferedCourseDTO> searchOfferedCourses(@RequestBody OfferedCouseSearchRequestDTO offeredCouseSearchRequestDTO) {
        return offerredCourseService.searchOfferedCourse(offeredCouseSearchRequestDTO);
    }
}
