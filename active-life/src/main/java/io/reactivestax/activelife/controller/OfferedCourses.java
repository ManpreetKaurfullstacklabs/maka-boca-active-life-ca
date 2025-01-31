package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/offeredcourse")
public class OfferedCourses {

    @Autowired
    private OfferredCourseService offerredCourseService;

    @PostMapping("/course")
    public ResponseEntity<String> addNewCourseToOfferedCourse(@RequestBody OfferedCourseDTO offeredCourseDTO) {


        return ResponseEntity.ok("family member added sucessfully to a course : " );
    }
}
