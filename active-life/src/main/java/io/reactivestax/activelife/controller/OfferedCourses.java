package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.UpdateCourseDTO;
import io.reactivestax.activelife.service.OfferredCourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/offeredcourse")
public class OfferedCourses {

    @Autowired
    private OfferredCourseService offerredCourseService;

    @PostMapping
    public ResponseEntity<String> addOfferedCourse( @Valid  @RequestBody OfferedCourseDTO offeredCourseDTO) {
        offerredCourseService.addOfferedCourseToDatabase(offeredCourseDTO);
        return ResponseEntity.ok("Course saved succesffuly");
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferedCourseDTO> getOfferedCourse(@Valid @PathVariable Long id) {
        OfferedCourseDTO offeredCoursesDTO = offerredCourseService.getOfferedCoursesById(id);
        return ResponseEntity.ok(offeredCoursesDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateCourseToOfferedCourse( @Valid @PathVariable Long id,  @RequestBody UpdateCourseDTO offeredCourseDTO) {
        offerredCourseService.updateOfferedCourseToDatabase(offeredCourseDTO,id);
        return ResponseEntity.ok("Courses updated sucessfully : " );
    }
    @GetMapping()
    public List<OfferedCourseDTO> getOfferedCourseList () {
       return   offerredCourseService.getOfferedCoursesList();
    }


}
