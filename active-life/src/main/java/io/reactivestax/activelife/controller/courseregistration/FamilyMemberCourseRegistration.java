package io.reactivestax.activelife.controller.courseregistration;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.dto.FamilyMemberDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courseregistration")
public class FamilyMemberCourseRegistration {

    @PostMapping("/member")
    public ResponseEntity<String>  addNewMemberToOfferedCourse(@Valid @RequestBody FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        return ResponseEntity.ok("family member added sucessfully to a course : " );
    }
    @GetMapping("/member/{id}")
    public ResponseEntity<FamilyCourseRegistrationDTO> getMemberDetailsWithId(@RequestHeader("Member-ID") @PathVariable String id) {
        return ResponseEntity.ok( );

    }

    @PatchMapping("/members")
    public ResponseEntity<FamilyCourseRegistrationDTO> updateMemberInformation(@RequestBody FamilyCourseRegistrationDTO  familyCourseRegistrationDTO) {
        return ResponseEntity.ok(familyCourseRegistrationDTO);
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<String> withdrawMemberFromCourse(@PathVariable Long id) {

        return ResponseEntity.ok("family member withdraw from course successfully");
    }


}
