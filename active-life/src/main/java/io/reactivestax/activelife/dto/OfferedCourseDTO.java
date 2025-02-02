package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OfferedCourseDTO {
    private String barcode;
    private LocalDate startDate;
    private LocalDate endDate;
    private  Long noOfSeats;
    private LocalDateTime startTime;
    private  LocalDateTime endTime;
    private IsAllDay isAllDay;
    private LocalDate registrationStartDate;
   private AvailableForEnrollment availableForEnrollment;
   private LocalDateTime updatedAt;
   private Long coursesId;
   private Long facilities;
   private OfferedCourseFee offeredCourseFee;


}
