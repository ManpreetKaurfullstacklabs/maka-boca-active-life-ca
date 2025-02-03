package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.dto.FamilyMemberDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashBoard {


//    @GetMapping("/member/{id}")
//    public ResponseEntity<FamilyMemberDTO> getFamilyMember(@PathVariable Long courseId, IsWaitListed isWaitListed) {
//      //  FamilyMemberDTO allMembersbygivenMemberId =  .getAllMembersbygivenMemberId(id);
//        return ResponseEntity.ok( );
//
//    }
}
