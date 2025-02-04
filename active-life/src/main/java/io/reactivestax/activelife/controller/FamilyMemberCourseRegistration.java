package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/courseregistration")
public class FamilyMemberCourseRegistration {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;


    @PostMapping("/member")
    public ResponseEntity<String>  addNewMemberToOfferedCourse( @RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        familyCourseRegistrationService.enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
        return ResponseEntity.ok("family member added sucessfully to a course : " );
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithId(@PathVariable Long id) {
        FamilyCourseRegistrationDTO allFamilyMemberRegistration = familyCourseRegistrationService.getAllFamilyMemberRegistration(id);
        return ResponseEntity.ok(allFamilyMemberRegistration);
    }

    @PatchMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> updateMemberInformation(@PathVariable Long id, @RequestBody FamilyCourseRegistrationDTO  familyCourseRegistrationDTO) {
        familyCourseRegistrationService.updateFamilyMemberRegistration(id, familyCourseRegistrationDTO);
        return ResponseEntity.ok(familyCourseRegistrationDTO);
    }

    @DeleteMapping("/member/{id}")
    public ResponseEntity<String> withdrawMemberFromCourse(@PathVariable Long id) {
        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(id);
        return ResponseEntity.ok("family member withdraw from course successfully");
    }


}
