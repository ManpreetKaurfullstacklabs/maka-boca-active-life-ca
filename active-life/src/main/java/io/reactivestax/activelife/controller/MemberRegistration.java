package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.service.MemberRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/familyregistration")
public class MemberRegistration {

    @Autowired
    private MemberRegistrationService memberRegistrationService;

    @PostMapping("/signup")
    public ResponseEntity<String>  addNewFamilyMemberAlongFamilyGroup(@Valid @RequestBody MemberRegistrationDTO memberRegistrationDTO) {
        String pin = memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO);
        return ResponseEntity.ok("family member added successfully with pin number : " + pin);
    }

    @PostMapping("/login")
    public ResponseEntity<String>  addFamilyMemberAlongFamilyGroup(@Valid @RequestBody LoginDTO login) {
        String value  = memberRegistrationService.loginExistingMember(login);
        return ResponseEntity.ok( value);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MemberRegistrationDTO> getFamilyMember( @Valid @PathVariable  String id) {
        MemberRegistrationDTO allMembersbygivenMemberId = memberRegistrationService.getAllMembersbygivenMemberId(id);
        return ResponseEntity.ok(allMembersbygivenMemberId);
    }

    @PatchMapping
    public ResponseEntity<MemberRegistrationDTO> updateMemberInformation( @Valid @RequestBody MemberRegistrationDTO memberRegistrationDTO) {
        memberRegistrationService.updateExistingFamilyMember(memberRegistrationDTO);
        return ResponseEntity.ok(memberRegistrationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateMember( @Valid @PathVariable String id) {
        memberRegistrationService.deleteFamilyMemberById(id);
        return ResponseEntity.ok("family member removed successfully");
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<String> verifySignup( @Valid @PathVariable String id) {
        memberRegistrationService.findFamilyMemberByVerificationId(id);
        return ResponseEntity.ok("verified");
    }
    @PostMapping("login/verify")
    public ResponseEntity<String> verifyLogin( @Valid @RequestBody LoginDTO loginDTO) {
        memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
        return ResponseEntity.ok("verified");
    }
}
