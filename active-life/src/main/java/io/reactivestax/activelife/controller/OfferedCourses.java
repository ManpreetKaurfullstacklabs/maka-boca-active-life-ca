package io.reactivestax.activelife.controller;


import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offeredcourse")
public class OfferedCourses {

    @Autowired
    private OfferredCourseService offerredCourseService;

    @PostMapping
    public ResponseEntity<String> addNewCourseToOfferedCourse(@RequestBody OfferedCourseDTO offeredCourseDTO) {
    offerredCourseService.addOfferedCourseToDatabase(offeredCourseDTO);
        return ResponseEntity.ok("Courses added sucessfully : " );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferedCourseDTO> getOfferedCourse( @PathVariable Long id) {
        OfferedCourseDTO offeredCoursesDTO = offerredCourseService.getOfferedCoursesById(id);
        return ResponseEntity.ok(offeredCoursesDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateCourseToOfferedCourse(@PathVariable Long id,  @RequestBody  OfferedCourseDTO offeredCourseDTO) {
        offerredCourseService.updateOfferedCourseToDatabase(offeredCourseDTO,id);
        return ResponseEntity.ok("Courses updated sucessfully : " );
    }


}
