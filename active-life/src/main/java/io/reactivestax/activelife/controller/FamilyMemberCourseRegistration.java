package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.MemberRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courseregistration")
public class FamilyMemberCourseRegistration {

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @Autowired
    private MemberRegistrationService memberRegistrationService;

    @PostMapping("/member")
    @PreAuthorize("hasRole('USER')")
   // @PreAuthorize("hasAuthority('SCOPE_ems.sms')")
    public ResponseEntity<String> addNewMemberToOfferedCourse(@RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        String response = familyCourseRegistrationService.enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{id}")
//    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithId(@PathVariable Long id) {
        FamilyCourseRegistrationDTO allFamilyMemberRegistration = familyCourseRegistrationService.getAllFamilyMemberRegistration(id);
        return ResponseEntity.ok(allFamilyMemberRegistration);
    }

    @PatchMapping("/member/{id}")
    @PreAuthorize("@applicationSecurityConfig.isGroupOwner(authentication)")
    public ResponseEntity<FamilyCourseRegistrationDTO> updateMemberInformation(@PathVariable Long id, @RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        familyCourseRegistrationService.updateFamilyMemberRegistration(id, familyCourseRegistrationDTO);
        return ResponseEntity.ok(familyCourseRegistrationDTO);
    }

    @DeleteMapping("/member/{id}")
    @CrossOrigin(origins = "http://localhost:5173")
//    @PreAuthorize("@applicationSecurityConfig.isGroupOwner(authentication)")
    public ResponseEntity<String> withdrawMemberFromCourse(@PathVariable Long id) {
        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(id);
        return ResponseEntity.ok("Family member withdrawn from course successfully");
    }
    @GetMapping("/memberLoginId/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithLoginId(@PathVariable Long id) {
        FamilyCourseRegistrationDTO allFamilyMemberRegistration = familyCourseRegistrationService.getAllFamilyMemberRegistrationByLoginId(id);
        return ResponseEntity.ok(allFamilyMemberRegistration);
    }

    @GetMapping("/familyMemberId/{id}")
    public ResponseEntity<List<FamilyCourseRegistrationDTO>> getMemberDetailsWithFamilyMemberId(@PathVariable Long id) {
        List<FamilyCourseRegistrationDTO> allFamilyMemberRegistrationByFamilyMemberId = familyCourseRegistrationService.getAllFamilyMemberRegistrationByFamilyMemberId(id);

        return ResponseEntity.ok(allFamilyMemberRegistrationByFamilyMemberId);
    }




}
