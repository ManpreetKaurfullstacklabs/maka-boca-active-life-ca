package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.service.MemberRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


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
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            ResponseEntity<String> responseEntity = memberRegistrationService.loginExistingMember(loginDTO);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, String> response = new HashMap<>();
                response.put("message", responseEntity.getBody());
                return ResponseEntity.ok(response);
            } else {

                return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
            }
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
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
        String familyMemberByOtpVerification = memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
        return ResponseEntity.ok(familyMemberByOtpVerification);
    }
}
