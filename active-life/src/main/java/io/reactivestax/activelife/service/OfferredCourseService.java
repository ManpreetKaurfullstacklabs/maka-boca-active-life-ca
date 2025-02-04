package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.dto.OfferedCourseFeeDTO;
import io.reactivestax.activelife.dto.UpdateCourseDTO;
import io.reactivestax.activelife.exception.InvalidFacilityIdException;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;

import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.repository.courses.CoursesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.facilities.FacilititesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Random;


@Service
public class OfferredCourseService {

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private FacilititesRepository facilititesRepository;

    @Autowired
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @Autowired
    private OfferedCourseSpecification offeredCourseSpecification;


    public void addOfferedCourseToDatabase(OfferedCourseDTO offeredCourseDTO) {

        OfferedCourseFee offeredCourseFee = getOfferedCourseFee(offeredCourseDTO);
        OfferedCourses offeredCourses = new OfferedCourses();
        offeredCourses.setBarcode(generateBarcode());
        offeredCourses.setStartDate(offeredCourseDTO.getStartDate());
        offeredCourses.setEndDate(offeredCourseDTO.getEndDate());
        offeredCourses.setNoOfClasses(offeredCourseDTO.getNoOfSeats());
        offeredCourses.setCost(offeredCourseDTO.getOfferedCourseFeeDTO().getCourseFee());
        offeredCourses.setStartTime(offeredCourseDTO.getStartTime());
        offeredCourses.setNoOfSeats(offeredCourseDTO.getNoOfSeats());
        offeredCourses.setEndTime(offeredCourseDTO.getEndTime());
        offeredCourses.setIsAllDay(offeredCourseDTO.getIsAllDay());
        offeredCourses.setIsWaitListed(IsWaitListed.NO);
        offeredCourses.setRegistrationStartDate(offeredCourseDTO.getRegistrationStartDate());
        offeredCourses.setAvailableForEnrollment(offeredCourseDTO.getAvailableForEnrollment());
        offeredCourses.setOfferedCourseId(offeredCourses.getOfferedCourseId());
        offeredCourses.setCreatedAt(LocalDateTime.now());
        offeredCourses.setLastUpdatedAt(LocalDate.now());
        offeredCourses.setCreatedBy(1L);
        offeredCourses.setLastUpdatedBy(1L);
        offeredCourses.setOfferedCourseFee(offeredCourseFee);

        Courses availableCourses = getAvailabeCoursesFromCourses(offeredCourseDTO.getCoursesId());
        offeredCourses.setCourses(availableCourses);
        Facilities availableFacilities = getAvailabeFacilititesFromFacilities(offeredCourseDTO.getFacilities());
        offeredCourses.setFacilities(availableFacilities);

        offeredCourseRepository.save(offeredCourses);
    }
    private OfferedCourseFee getOfferedCourseFee(OfferedCourseDTO offeredCourseDTO) {
        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();
        offeredCourseFee.setFeeType(offeredCourseDTO.getOfferedCourseFeeDTO().getFeeType());
        offeredCourseFee.setCourseFee(offeredCourseDTO.getOfferedCourseFeeDTO().getCourseFee());
        offeredCourseFee.setCreatedTimestamp(LocalDate.now());
        offeredCourseFee.setLastUpdatedTimestamp(LocalDate.now());
        offeredCourseFee.setCreatedBy(1L);
        offeredCourseFee.setLastUpdatedBy(1L);
        return offeredCourseFee;
    }
    public OfferedCourseDTO getOfferedCoursesById(Long id ){
        OfferedCourses offeredCourses = offeredCourseRepository.findById(id).orElseThrow(() -> new InvalidCourseIdException("no course found .."));
        OfferedCourseFee offeredCourseFee = offeredCourseFeeRepository.findById(id).orElseThrow(() -> new InvalidCourseIdException("no course found .."));
        OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
        OfferedCourseFeeDTO offeredCourseFeeDTO = new OfferedCourseFeeDTO();
        offeredCourseFeeDTO.setFeeId(offeredCourseFee.getFeeId());
        offeredCourseFeeDTO.setFeeType(offeredCourseFee.getFeeType());
        offeredCourseFeeDTO.setCourseFee(offeredCourseFee.getCourseFee());
        offeredCourseFeeDTO.setCreatedTimestamp(offeredCourseFee.getCreatedTimestamp());
        offeredCourseFeeDTO.setLastUpdatedBy(offeredCourseFee.getLastUpdatedBy());
        offeredCourseFeeDTO.setCreatedBy(offeredCourseFee.getCreatedBy());
        offeredCourseFeeDTO.setLastUpdatedTimestamp(offeredCourseFee.getLastUpdatedTimestamp());

        offeredCourseDTO.setCoursesId(offeredCourses.getOfferedCourseId());
        offeredCourseDTO.setEndTime(offeredCourses.getEndTime());
        offeredCourseDTO.setIsAllDay(offeredCourses.getIsAllDay());
        offeredCourseDTO.setRegistrationStartDate(offeredCourses.getRegistrationStartDate());
        offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseDTO.getOfferedCourseFeeDTO());
        offeredCourseDTO.setNoOfSeats(offeredCourses.getNoOfClasses());
        offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseDTO.getOfferedCourseFeeDTO());
        offeredCourseDTO.setStartDate(offeredCourses.getStartDate());
        offeredCourseDTO.setStartTime(offeredCourses.getStartTime());
        offeredCourseDTO.setFacilities(offeredCourses.getFacilities().getId());
        offeredCourseDTO.setIsAllDay(offeredCourses.getIsAllDay());
        offeredCourseDTO.setAvailableForEnrollment(offeredCourses.getAvailableForEnrollment());
        offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseFeeDTO);

        return offeredCourseDTO;
    }
    public void updateOfferedCourseToDatabase(UpdateCourseDTO updateCourseDTO, Long id ) {
        OfferedCourses offeredCourses = offeredCourseRepository.findById(id).orElseThrow(() -> new InvalidCourseIdException("offered course does not exists"));
        offeredCourses.setStartDate(updateCourseDTO.getStartDate());
        offeredCourses.setEndDate(updateCourseDTO.getEndDate());
        offeredCourses.setNoOfClasses(updateCourseDTO.getNoOfSeats());
        offeredCourses.setStartTime(updateCourseDTO.getStartTime());
        offeredCourses.setEndTime(updateCourseDTO.getEndTime());
        offeredCourses.setIsAllDay(updateCourseDTO.getIsAllDay());
        offeredCourses.setRegistrationStartDate(updateCourseDTO.getRegistrationStartDate());
        offeredCourses.setAvailableForEnrollment(updateCourseDTO.getAvailableForEnrollment());
        offeredCourses.setCreatedAt(LocalDateTime.now());
        offeredCourses.setLastUpdatedAt(LocalDate.now());
        offeredCourses.setCreatedBy(1L);
        offeredCourses.setCost(offeredCourses.getCost());
        offeredCourses.setLastUpdatedBy(1L);
        offeredCourseRepository.save(offeredCourses);
    }

    public Courses getAvailabeCoursesFromCourses(Long id) {
        return coursesRepository.findById(id)
                .orElseThrow(() -> new InvalidCourseIdException("Course with ID " + id + " not found"));
    }
    public Facilities getAvailabeFacilititesFromFacilities(Long id) {
        return facilititesRepository.findById(id)
                .orElseThrow(() -> new InvalidFacilityIdException("Facilities with ID " + id + " not found"));
    }
    public String generateBarcode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            pin.append(characters.charAt(index));
        }
        return pin.toString();
    }




}
