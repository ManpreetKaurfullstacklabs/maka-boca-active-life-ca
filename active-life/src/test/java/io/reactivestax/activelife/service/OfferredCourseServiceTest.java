package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.FeeType;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.repository.courses.CoursesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.facilities.FacilititesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferedCourseServiceTest {

    @InjectMocks
    private OfferredCourseService offerredCourseService;

    @Mock
    private OfferedCourseRepository offeredCourseRepository;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private FacilititesRepository facilititesRepository;

    @Mock
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    private OfferedCourseDTO offeredCourseDTO;
    private OfferedCourses offeredCourses;
    LocalDateTime localDateTime = LocalDateTime.now();
    LocalDate localDate = LocalDate.now();
    LocalDateTime endTime = LocalDateTime.now().plusHours(2);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setStartDate(LocalDate.now());
        offeredCourseDTO.setEndDate(LocalDate.now().plusDays(5));
        offeredCourseDTO.setNoOfSeats(10L);
        offeredCourseDTO.setStartTime(localDateTime);
        offeredCourseDTO.setEndTime(endTime);
        offeredCourseDTO.setIsAllDay(IsAllDay.YES);
        offeredCourseDTO.setAvailableForEnrollment(AvailableForEnrollment.YES);
        offeredCourseDTO.setRegistrationStartDate(LocalDate.now());

        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();
        offeredCourseFee.setFeeType(FeeType.RESIDENT);
        offeredCourseFee.setCourseFee(100L);
        offeredCourseDTO.setOfferedCourseFee(offeredCourseFee);


        offeredCourseDTO.setCoursesId(1L);

        offeredCourses = new OfferedCourses();
        offeredCourses.setStartDate(LocalDate.now());
        offeredCourses.setEndDate(LocalDate.now().plusDays(5));
        offeredCourses.setNoOfSeats(10L);
        offeredCourses.setStartTime(localDateTime);
        offeredCourses.setEndTime(endTime);
        offeredCourses.setIsAllDay(IsAllDay.YES);
        offeredCourses.setAvailableForEnrollment(AvailableForEnrollment.YES);
    }


    @Test
    void testAddOfferedCourseToDatabaseSuccessfulCase() {

        Courses mockCourse = new Courses();
        mockCourse.setCourseId(1L);

        Facilities mockFacility = new Facilities();
        mockFacility.setId(1L);

        OfferedCourseFee mockOfferedCourseFee = new OfferedCourseFee();
        mockOfferedCourseFee.setCourseFee(100L);
        mockOfferedCourseFee.setFeeType(FeeType.RESIDENT);


        offeredCourseDTO.setOfferedCourseFee(mockOfferedCourseFee);


        when(coursesRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(facilititesRepository.findById(1L)).thenReturn(Optional.of(mockFacility));
        when(offeredCourseFeeRepository.save(any(OfferedCourseFee.class))).thenReturn(mockOfferedCourseFee);


        offerredCourseService.addOfferedCourseToDatabase(offeredCourseDTO);

        verify(offeredCourseRepository, times(1)).save(any(OfferedCourses.class));

        ArgumentCaptor<OfferedCourses> offeredCoursesCaptor = ArgumentCaptor.forClass(OfferedCourses.class);
        verify(offeredCourseRepository).save(offeredCoursesCaptor.capture());

        OfferedCourses savedOfferedCourses = offeredCoursesCaptor.getValue();

        assertNotNull(savedOfferedCourses.getStartDate());
        assertNotNull(savedOfferedCourses.getEndDate());
        assertEquals(10, savedOfferedCourses.getNoOfSeats());
        assertEquals(localDateTime, savedOfferedCourses.getStartTime());
        assertEquals(endTime, savedOfferedCourses.getEndTime());
        assertEquals(100, savedOfferedCourses.getCost());
        assertTrue(savedOfferedCourses.getIsAllDay().equals(IsAllDay.YES));
        assertNotNull(savedOfferedCourses.getBarcode());
        assertNotNull(savedOfferedCourses.getCreatedAt());
        assertNotNull(savedOfferedCourses.getLastUpdatedAt());

        assertEquals(mockCourse, savedOfferedCourses.getCourses());
        assertEquals(mockFacility, savedOfferedCourses.getFacilities());
        assertEquals(mockOfferedCourseFee, savedOfferedCourses.getOfferedCourseFee());
    }

    @Test
    void testGetAvailabeCoursesFromCourses_CourseNotFound() {

        when(coursesRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InvalidCourseIdException.class, () -> {
            offerredCourseService.getAvailabeCoursesFromCourses(1L);
        });
    }

    @Test
    void testGetAvailabeFacilititesFromFacilities_FacilityNotFound() {

        when(facilititesRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(InvalidCourseIdException.class, () -> {
            offerredCourseService.getAvailabeFacilititesFromFacilities(1L);
        });
    }

    @Test
    void testGenerateBarcode() {

        String barcode = offerredCourseService.generateBarcode();

        assertNotNull(barcode);
        assertEquals(6, barcode.length());
    }

    @Test
    void testAddOfferedCourseToDatabase_SuccessfulCase() {

        Courses mockCourse = new Courses();
        mockCourse.setCourseId(1L);

        Facilities mockFacility = new Facilities();
        mockFacility.setId(1L);

        OfferedCourseFee mockOfferedCourseFee = new OfferedCourseFee();
        mockOfferedCourseFee.setCourseFee(100L);
        mockOfferedCourseFee.setFeeType(FeeType.RESIDENT);

        when(coursesRepository.findById(1L)).thenReturn(Optional.of(mockCourse));
        when(facilititesRepository.findById(1L)).thenReturn(Optional.of(mockFacility));
        when(offeredCourseFeeRepository.save(any(OfferedCourseFee.class))).thenReturn(mockOfferedCourseFee);

        offerredCourseService.addOfferedCourseToDatabase(offeredCourseDTO);
        verify(offeredCourseRepository, times(1)).save(any(OfferedCourses.class));

        ArgumentCaptor<OfferedCourses> offeredCoursesCaptor = ArgumentCaptor.forClass(OfferedCourses.class);
        verify(offeredCourseRepository).save(offeredCoursesCaptor.capture());
        OfferedCourses savedOfferedCourses = offeredCoursesCaptor.getValue();

        assertNotNull(savedOfferedCourses.getStartDate());
        assertNotNull(savedOfferedCourses.getEndDate());
        assertEquals(10L, savedOfferedCourses.getNoOfSeats());
        assertEquals(localDateTime, savedOfferedCourses.getStartTime());
        assertEquals(endTime, savedOfferedCourses.getEndTime());
        assertEquals(100L, savedOfferedCourses.getCost());
        assertTrue(savedOfferedCourses.getIsAllDay().equals(IsAllDay.YES));
        assertNotNull(savedOfferedCourses.getBarcode());
        assertNotNull(savedOfferedCourses.getCreatedAt());
        assertNotNull(savedOfferedCourses.getLastUpdatedAt());
        assertEquals(mockCourse, savedOfferedCourses.getCourses());
        assertEquals(mockFacility, savedOfferedCourses.getFacilities());
        assertEquals(mockOfferedCourseFee, savedOfferedCourses.getOfferedCourseFee());
    }

    @Test
    void testGetAvailabeCoursesFromCourses_CourseNotFound1() {

        when(coursesRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(InvalidCourseIdException.class, () -> {
            offerredCourseService.getAvailabeCoursesFromCourses(1L);
        });
    }

}
