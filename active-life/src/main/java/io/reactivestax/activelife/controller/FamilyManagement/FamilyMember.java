package io.reactivestax.activelife.controller.FamilyManagement;

import io.reactivestax.activelife.dto.FamilyMemberDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.service.FamilyMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/familymember")
public class FamilyMember {

    @Autowired
    private FamilyMemberService familyMemberService;

    @PostMapping("/signup")
    public ResponseEntity<String>  addNewFamilyMemberAlongFamilyGroup(@Valid @RequestBody FamilyMemberDTO familyMemberDTO) {
        familyMemberService.addNewFamilyMemberOnSignup(familyMemberDTO);
        return ResponseEntity.ok("family member added sucessfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String>  addFamilyMemberAlongFamilyGroup( @RequestBody LoginDTO login) {
        String value  = familyMemberService.loginExistingMember(login);
        return ResponseEntity.ok( value);
    }



    @GetMapping("/familymember/{id}")
    public ResponseEntity<FamilyMemberDTO> getFamilyMember(@PathVariable String id) {
        FamilyMemberDTO allMembersbygivenMemberId = familyMemberService.getAllMembersbygivenMemberId(id);
        return ResponseEntity.ok(allMembersbygivenMemberId);

    }

    @PatchMapping("/members")
    public ResponseEntity<FamilyMemberDTO> updateMemberInformation(@RequestBody FamilyMemberDTO  familyMemberDTO) {
        familyMemberService.updateExistingFamilyMember(familyMemberDTO);
        return ResponseEntity.ok(familyMemberDTO);
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<String> deactivateMember(@PathVariable Long id) {
        familyMemberService.deleteFamilyMemberById(id);
        return ResponseEntity.ok("family member removed sucessfully");
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<String> verifySignup( @PathVariable String id) {
        familyMemberService.findFamilyMemberByVerificationId(id);
        return ResponseEntity.ok("verified");
    }
}
