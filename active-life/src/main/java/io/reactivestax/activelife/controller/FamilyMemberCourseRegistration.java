package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.MemberRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courseregistration")
public class FamilyMemberCourseRegistration {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @Autowired
    private MemberRegistrationService memberRegistrationService;

    // Add new member to offered course
    @PostMapping("/member")
    public ResponseEntity<String> addNewMemberToOfferedCourse(@RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        // Check if the authenticated user is a GroupOwner
        if (!isGroupOwner()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to perform this action");
        }
        String response = familyCourseRegistrationService.enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
        return ResponseEntity.ok(response);
    }

    // Get member details by ID
    @GetMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithId(@PathVariable Long id) {
        FamilyCourseRegistrationDTO allFamilyMemberRegistration = familyCourseRegistrationService.getAllFamilyMemberRegistration(id);
        return ResponseEntity.ok(allFamilyMemberRegistration);
    }

    // Update member information
    @PatchMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> updateMemberInformation(@PathVariable Long id, @RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        // Check if the authenticated user is a GroupOwner
        if (!isGroupOwner()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        familyCourseRegistrationService.updateFamilyMemberRegistration(id, familyCourseRegistrationDTO);
        return ResponseEntity.ok(familyCourseRegistrationDTO);
    }

    // Withdraw member from course
    @DeleteMapping("/member/{id}")
    public ResponseEntity<String> withdrawMemberFromCourse(@PathVariable Long id) {
        // Check if the authenticated user is a GroupOwner
        if (!isGroupOwner()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to withdraw members from this course");
        }

        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(id);
        return ResponseEntity.ok("Family member withdrawn from course successfully");
    }

    // Helper method to check if the authenticated user is a GroupOwner
    private boolean isGroupOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return memberRegistrationService.isGroupOwner(username);
    }
}
