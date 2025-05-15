package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.dto.*;
import io.reactivestax.activelife.exception.InvalidFacilityIdException;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;

import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.repository.courses.CoursesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.facilities.FacilititesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.utility.interfaces.OfferedCourseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


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
    public OfferedCourseDTO getOfferedCoursesById(Long id) {
        OfferedCourses offeredCourses = offeredCourseRepository.findById(id)
                .orElseThrow(() -> new InvalidCourseIdException("No course found."));

        OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setOfferedCourseId(offeredCourses.getOfferedCourseId());
        offeredCourseDTO.setBarcode(offeredCourses.getBarcode());
        offeredCourseDTO.setStartDate(offeredCourses.getStartDate());
        offeredCourseDTO.setEndDate(offeredCourses.getEndDate());
        offeredCourseDTO.setStartTime(offeredCourses.getStartTime());
        offeredCourseDTO.setEndTime(offeredCourses.getEndTime());
        offeredCourseDTO.setNoOfSeats(offeredCourses.getNoOfClasses());
        offeredCourseDTO.setIsAllDay(offeredCourses.getIsAllDay());
        offeredCourseDTO.setRegistrationStartDate(offeredCourses.getRegistrationStartDate());
        offeredCourseDTO.setAvailableForEnrollment(offeredCourses.getAvailableForEnrollment());
        offeredCourseDTO.setCoursesId(offeredCourses.getCourses().getCourseId());
        offeredCourseDTO.setFacilities(offeredCourses.getFacilities().getId());


        Courses courses = offeredCourses.getCourses();
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setCourseId(courses.getCourseId());
        courseDTO.setName(courses.getName());
        courseDTO.setDescription(courses.getDescription());
        courseDTO.setSubcategories(courses.getSubcategories());
        courseDTO.setAgeGroups(courses.getAgeGroups());
        offeredCourseDTO.setCourseDTO(courseDTO);


        OfferedCourseFee offeredCourseFee = offeredCourses.getOfferedCourseFee();
        OfferedCourseFeeDTO offeredCourseFeeDTO = new OfferedCourseFeeDTO();
        offeredCourseFeeDTO.setFeeId(offeredCourseFee.getFeeId());
        offeredCourseFeeDTO.setFeeType(offeredCourseFee.getFeeType());
        offeredCourseFeeDTO.setCourseFee(offeredCourseFee.getCourseFee());
        offeredCourseFeeDTO.setCreatedTimestamp(offeredCourseFee.getCreatedTimestamp());
        offeredCourseFeeDTO.setLastUpdatedTimestamp(offeredCourseFee.getLastUpdatedTimestamp());
        offeredCourseFeeDTO.setCreatedBy(offeredCourseFee.getCreatedBy());
        offeredCourseFeeDTO.setLastUpdatedBy(offeredCourseFee.getLastUpdatedBy());
        offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseFeeDTO);


        Facilities facilities = offeredCourses.getFacilities();
        if (facilities != null) {
            FacilititesDTO facilititesDTO = new FacilititesDTO();
            facilititesDTO.setId(facilities.getId());
            facilititesDTO.setDescription(facilities.getDescription());
            facilititesDTO.setStreetNo(facilities.getStreetNo());
            facilititesDTO.setStreetName(facilities.getStreetName());
            facilititesDTO.setCity(facilities.getCity());
            facilititesDTO.setProvince(facilities.getProvince());
            facilititesDTO.setCountry(facilities.getCountry());
            facilititesDTO.setPostalCode(facilities.getPostalCode());
            offeredCourseDTO.setFacilititesDTO(facilititesDTO);
        }

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


    public List<OfferedCourseDTO> getOfferedCoursesList() {

        List<OfferedCourses> offeredCourseList = offeredCourseRepository.findAll();

        return offeredCourseList.stream()
                .map(offeredCourse -> {

                    OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
                    offeredCourseDTO.setOfferedCourseId(offeredCourse.getOfferedCourseId());
                    offeredCourseDTO.setCoursesId(offeredCourse.getOfferedCourseId());
                    offeredCourseDTO.setStartDate(offeredCourse.getStartDate());
                    offeredCourseDTO.setEndDate(offeredCourse.getEndDate());
                    offeredCourseDTO.setStartTime(offeredCourse.getStartTime());
                    offeredCourseDTO.setEndTime(offeredCourse.getEndTime());
                    offeredCourseDTO.setNoOfSeats(offeredCourse.getNoOfSeats());
                    offeredCourseDTO.setIsAllDay(offeredCourse.getIsAllDay());
                    offeredCourseDTO.setRegistrationStartDate(offeredCourse.getRegistrationStartDate());
                    offeredCourseDTO.setAvailableForEnrollment(offeredCourse.getAvailableForEnrollment());
                    offeredCourseDTO.setBarcode(offeredCourse.getBarcode());


                    OfferedCourseFee offeredCourseFee = offeredCourse.getOfferedCourseFee();
                    OfferedCourseFeeDTO offeredCourseFeeDTO = new OfferedCourseFeeDTO();
                    offeredCourseFeeDTO.setFeeId(offeredCourseFee.getFeeId());
                    offeredCourseFeeDTO.setFeeType(offeredCourseFee.getFeeType());
                    offeredCourseFeeDTO.setCourseFee(offeredCourseFee.getCourseFee());
                    offeredCourseFeeDTO.setCreatedTimestamp(offeredCourseFee.getCreatedTimestamp());
                    offeredCourseFeeDTO.setLastUpdatedBy(offeredCourseFee.getLastUpdatedBy());
                    offeredCourseFeeDTO.setCreatedBy(offeredCourseFee.getCreatedBy());
                    offeredCourseFeeDTO.setLastUpdatedTimestamp(offeredCourseFee.getLastUpdatedTimestamp());
                    offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseFeeDTO);

                    Courses courses = offeredCourse.getCourses();
                    CourseDTO courseDTO = new CourseDTO();
                    courseDTO.setCourseId(courses.getCourseId());
                    courseDTO.setName(courses.getName());
                    courseDTO.setDescription(courses.getDescription());
                    courseDTO.setSubcategories(courses.getSubcategories());
                    courseDTO.setAgeGroups(courses.getAgeGroups());
                    offeredCourseDTO.setCourseDTO(courseDTO);




                    Facilities facilities = offeredCourse.getFacilities();
                    if (facilities != null) {
                        offeredCourseDTO.setFacilities(facilities.getId());
                    }
                    FacilititesDTO facilititesDTO = new FacilititesDTO();
                    facilititesDTO.setId(facilities.getId());
                    facilititesDTO.setDescription(facilities.getDescription());
                    facilititesDTO.setStreetNo(facilities.getStreetNo());
                    facilititesDTO.setStreetName(facilities.getStreetName());
                    facilititesDTO.setCity(facilities.getCity());
                    facilititesDTO.setProvince(facilities.getProvince());
                    facilititesDTO.setCountry(facilities.getCountry());
                    facilititesDTO.setPostalCode(facilities.getPostalCode());
                    offeredCourseDTO.setFacilititesDTO(facilititesDTO);

                    return offeredCourseDTO;
                })
                .collect(Collectors.toList());
    }



}
