package io.reactivestax.activelife.controller.FamilyManagement;

import io.reactivestax.activelife.dto.FamilyMemberDTO;
import io.reactivestax.activelife.service.FamilyMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/members") // didnt test now
    public ResponseEntity<String> addFamilyMembersToExisting(@Valid @RequestBody FamilyMemberDTO familyMemberDTO) {
        familyMemberService.addFamilyToExistingGroupID(familyMemberDTO);
        return ResponseEntity.ok("member added to existing family group.  "+ familyMemberDTO.getMemberLoginId());
    }


    @GetMapping("/members/{id}")
    public ResponseEntity<FamilyMemberDTO> getFamilyMember(@PathVariable Long id) {
        FamilyMemberDTO allMembersbygivenMemberId = familyMemberService.getAllMembersbygivenMemberId(id);
        return ResponseEntity.ok(allMembersbygivenMemberId);

    }

    @PatchMapping("/members/")
    public ResponseEntity<FamilyMemberDTO> updateMemberInformation(@RequestBody FamilyMemberDTO  familyMemberDTO) {
        familyMemberService.updateExistingFamilyMember(familyMemberDTO);
        return ResponseEntity.ok(familyMemberDTO);
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<String> deactivateMember(@PathVariable Long id) {
        familyMemberService.deleteFamilyMemberById(id);
        return ResponseEntity.ok("family member removed sucessfully");
    }

}
