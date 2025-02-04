package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.utility.interfaces.OfferedCourseMapper;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;
import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
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
        offeredCourses.setCost(offeredCourseDTO.getOfferedCourseFee().getCourseFee());
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

        Courses availableCourses = getAvailabeCoursesFromCourses(offeredCourseDTO.getCoursesId());
        offeredCourses.setCourses(availableCourses);
        Facilities availableFacilities = getAvailabeFacilititesFromFacilities(offeredCourses.getCourses().getCourseId());
        offeredCourses.setFacilities(availableFacilities);
        offeredCourses.setOfferedCourseFee(offeredCourseFee);
        offeredCourseRepository.save(offeredCourses);
    }
    private OfferedCourseFee getOfferedCourseFee(OfferedCourseDTO offeredCourseDTO) {
        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();
        offeredCourseFee.setFeeType(offeredCourseDTO.getOfferedCourseFee().getFeeType());
        offeredCourseFee.setCourseFee(offeredCourseDTO.getOfferedCourseFee().getCourseFee());
        offeredCourseFee.setCreatedTimestamp(LocalDate.now());
        offeredCourseFee.setLastUpdatedTimestamp(LocalDate.now());
        offeredCourseFee.setCreatedBy(1L);
        offeredCourseFee.setLastUpdatedBy(1L);

        offeredCourseFeeRepository.save(offeredCourseFee);
        return offeredCourseFee;
    }
    public OfferedCourseDTO getOfferedCoursesById(Long id ){
        OfferedCourses offeredCourses = offeredCourseRepository.findById(id).orElseThrow(() -> new InvalidCourseIdException("no course found .."));
        OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setCoursesId(offeredCourses.getOfferedCourseId());
        offeredCourseDTO.setOfferedCourseFee(offeredCourses.getOfferedCourseFee());
        offeredCourseDTO.setNoOfSeats(offeredCourses.getNoOfClasses());
        offeredCourseDTO.setBarcode(offeredCourses.getBarcode());
        offeredCourseDTO.setOfferedCourseFee(offeredCourses.getOfferedCourseFee());
        offeredCourseDTO.setStartDate(offeredCourses.getStartDate());
        offeredCourseDTO.setStartTime(offeredCourses.getStartTime());
        offeredCourseDTO.setFacilities(offeredCourses.getFacilities().getId());
        offeredCourseDTO.setIsAllDay(offeredCourses.getIsAllDay());
        offeredCourseDTO.setAvailableForEnrollment(offeredCourses.getAvailableForEnrollment());

        return offeredCourseDTO;
    }
    public void updateOfferedCourseToDatabase(OfferedCourseDTO offeredCourseDTO,Long id ) {
        OfferedCourses offeredCourses = offeredCourseRepository.findById(id).orElseThrow(() -> new InvalidCourseIdException("offered course does not exists"));
        offeredCourses.setStartDate(offeredCourseDTO.getStartDate());
        offeredCourses.setEndDate(offeredCourseDTO.getEndDate());
        offeredCourses.setNoOfClasses(offeredCourseDTO.getNoOfSeats());
        offeredCourses.setStartTime(offeredCourseDTO.getStartTime());
        offeredCourses.setEndTime(offeredCourseDTO.getEndTime());
        offeredCourses.setIsAllDay(offeredCourseDTO.getIsAllDay());
        offeredCourses.setRegistrationStartDate(offeredCourseDTO.getRegistrationStartDate());
        offeredCourses.setAvailableForEnrollment(offeredCourseDTO.getAvailableForEnrollment());
        offeredCourses.setCreatedAt(LocalDateTime.now());
        offeredCourses.setLastUpdatedAt(LocalDate.now());
        offeredCourses.setCreatedBy(1L);
        offeredCourses.setCost(offeredCourseDTO.getOfferedCourseFee().getCourseFee());
        offeredCourses.setLastUpdatedBy(1L);
        Courses availableCourses = getAvailabeCoursesFromCourses(offeredCourseDTO.getCoursesId());
        offeredCourses.setCourses(availableCourses);
        Facilities availableFacilities = getAvailabeFacilititesFromFacilities(offeredCourseDTO.getFacilities());
        offeredCourses.setFacilities(availableFacilities);
        offeredCourseRepository.save(offeredCourses);
    }

    public Courses getAvailabeCoursesFromCourses(Long id) {
        return coursesRepository.findById(id)
                .orElseThrow(() -> new InvalidCourseIdException("Course with ID " + id + " not found"));
    }
    public Facilities getAvailabeFacilititesFromFacilities(Long id) {
        return facilititesRepository.findById(id)
                .orElseThrow(() -> new InvalidCourseIdException("Course with ID " + id + " not found"));
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


    public List<OfferedCourseDTO> searchOfferedCourse(OfferedCouseSearchRequestDTO offeredCouseSearchRequestDTO) {
        Specification<OfferedCourses> offeredCoursesSpecification =
                Specification.where(OfferedCourseSpecification.withCourseName(offeredCouseSearchRequestDTO.getCourseName()))
                        .and(OfferedCourseSpecification.withStartDate(offeredCouseSearchRequestDTO.getStartDate()))
                        .and(offeredCourseSpecification.withEndDate(offeredCouseSearchRequestDTO.getEndDate()))
                        .and(offeredCourseSpecification.withCity(offeredCouseSearchRequestDTO.getCity()))
                        .and(offeredCourseSpecification.withProvince(offeredCouseSearchRequestDTO.getProvince()))
                        .and(offeredCourseSpecification.hasCategory(offeredCouseSearchRequestDTO.getCategoryName()))
                        .and(offeredCourseSpecification.hasSubCategory(offeredCouseSearchRequestDTO.getSubCategory()))
                        .and(offeredCourseSpecification.withAgeGroup(offeredCouseSearchRequestDTO.getAgeGroup()));

        List<OfferedCourses> offeredCoursesList = offeredCourseRepository.findAll(offeredCoursesSpecification);
        return offeredCoursesList.stream()
                .map(OfferedCourseMapper.INSTANCE::offeredCourseToOfferedCourseDTO)
                .collect(Collectors.toList());
    }

}
