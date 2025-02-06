package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/courseregistration")
public class FamilyMemberCourseRegistration {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;


    @PostMapping("/member")
    public ResponseEntity<String>  addNewMemberToOfferedCourse( @Valid @RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        String response = familyCourseRegistrationService.enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithId( @Valid @PathVariable Long id) {
        FamilyCourseRegistrationDTO allFamilyMemberRegistration = familyCourseRegistrationService.getAllFamilyMemberRegistration(id);
        return ResponseEntity.ok(allFamilyMemberRegistration);
    }

    @PatchMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> updateMemberInformation( @Valid @PathVariable Long id, @RequestBody FamilyCourseRegistrationDTO  familyCourseRegistrationDTO) {
        familyCourseRegistrationService.updateFamilyMemberRegistration(id, familyCourseRegistrationDTO);
        return ResponseEntity.ok(familyCourseRegistrationDTO);
    }

    @DeleteMapping("/member/{id}")
    public ResponseEntity<String> withdrawMemberFromCourse( @Valid @PathVariable Long id) {
        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(id);
        return ResponseEntity.ok("family member withdraw from course successfully");
    }


}
