package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.facility.Facilities;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OfferedCourseDTO {
    private String barcode;
    private LocalDate startDate;
    private LocalDate endDate;
    private  Long noOfCourses;
    private LocalDateTime startTime;
    private  LocalDateTime endTime;
    private IsAllDay isAllDay;
    private LocalDate registrationStartDate;
   private AvailableForEnrollment availableForEnrollment;
   private LocalDateTime updatedAt;

   private List<Courses> courses;
   private List<Facilities> facilities;


}
