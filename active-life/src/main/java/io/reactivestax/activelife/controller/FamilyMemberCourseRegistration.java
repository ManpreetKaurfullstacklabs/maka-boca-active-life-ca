package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.MemberRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courseregistration")
public class FamilyMemberCourseRegistration {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @Autowired
    private MemberRegistrationService memberRegistrationService;

    @PostMapping("/member")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> addNewMemberToOfferedCourse(@RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        String response = familyCourseRegistrationService.enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithId(@PathVariable Long id) {
        FamilyCourseRegistrationDTO allFamilyMemberRegistration = familyCourseRegistrationService.getAllFamilyMemberRegistration(id);
        return ResponseEntity.ok(allFamilyMemberRegistration);
    }

    @PatchMapping("/member/{id}")
    @PreAuthorize("@applicationSecurityConfig.isGroupOwner(authentication)")  // Use @PreAuthorize with SpEL
    public ResponseEntity<FamilyCourseRegistrationDTO> updateMemberInformation(@PathVariable Long id, @RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        familyCourseRegistrationService.updateFamilyMemberRegistration(id, familyCourseRegistrationDTO);
        return ResponseEntity.ok(familyCourseRegistrationDTO);
    }

    @DeleteMapping("/member/{id}")
    @PreAuthorize("@applicationSecurityConfig.isGroupOwner(authentication)")  // Use @PreAuthorize with SpEL
    public ResponseEntity<String> withdrawMemberFromCourse(@PathVariable Long id) {
        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(id);
        return ResponseEntity.ok("Family member withdrawn from course successfully");
    }
}
