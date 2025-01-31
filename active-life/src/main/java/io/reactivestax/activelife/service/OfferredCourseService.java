package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.repository.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OfferredCourseService {

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;


    public  void addOfferedCourseToDatabase(OfferedCourseDTO offeredCourseDTO){
        OfferedCourses offeredCourses = new OfferedCourses();
        offeredCourses.setBarcode();
        offeredCourses.setStartDate(offeredCourseDTO.getStartDate());
        offeredCourses.setEndDate(offeredCourseDTO.getEndDate());
        offeredCourses.setNoOfClasses(offeredCourses.getNoOfClasses());
        offeredCourses.setStartTime(offeredCourses.getStartTime());
        offeredCourses.setEndTime(offeredCourses.getEndTime());
        offeredCourses.setIsAllDay(offeredCourseDTO.getIsAllDay());
        offeredCourses.setRegistrationStartDate(offeredCourses.getRegistrationStartDate());
        offeredCourses.setAvailableForEnrollment(offeredCourses.getAvailableForEnrollment());
        offeredCourses.setCreatedAt(LocalDateTime.now());
        offeredCourses.setLastUpdatedAt(LocalDate.now());
        offeredCourses.setCourses();
        offeredCourses.setFacilities();
        offeredCourses.setCreatedBy(1L);
    }

    public void getAvailabeCourses(Long id ){

    }
}
