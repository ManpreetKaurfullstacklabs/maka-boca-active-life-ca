package io.reactivestax.activelife.controller.OfferedCourse;

import io.reactivestax.activelife.dto.OfferedCoursesDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/offeredCourse")
public class OfferedCourse {


    @PostMapping("/course")
    public ResponseEntity<String> addofferedCourse(@Valid @RequestBody OfferedCoursesDTO offeredCoursesDTO) {

//        String pin = familyMemberService.addNewFamilyMemberOnSignup(familyMemberDTO);
//        return ResponseEntity.ok("family member added sucessfully with pin number : " + pin);
    }

}
