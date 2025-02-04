package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.FeeType;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
import io.reactivestax.activelife.repository.courses.CoursesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.facilities.FacilititesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.utility.interfaces.OfferedCourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    @Mock
    private OfferedCourseMapper offeredCourseMapper;

    private OfferedCourseDTO offeredCourseDTO;
    private OfferedCourses offeredCourse;
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
        offeredCourseDTO.setCoursesId(1L);
        offeredCourseDTO.setFacilities(1L);

        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();
        offeredCourseFee.setFeeType(FeeType.RESIDENT);
        offeredCourseFee.setCourseFee(100L);
        offeredCourseDTO.setOfferedCourseFee(offeredCourseFee);
        offeredCourseDTO.setCoursesId(1L);

        offeredCourse = new OfferedCourses();
        offeredCourse.setStartDate(LocalDate.now());
        offeredCourse.setEndDate(LocalDate.now().plusDays(5));
        offeredCourse.setNoOfSeats(10L);
        offeredCourse.setStartTime(localDateTime);
        offeredCourse.setEndTime(endTime);
        offeredCourse.setIsAllDay(IsAllDay.YES);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);
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


        assertEquals(mockCourse, savedOfferedCourses.getCourses());
       assertEquals(mockFacility, savedOfferedCourses.getFacilities());

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
    void testAddOfferedCourseToDatabase_Fail_InvalidCourse() {
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
    }

    @Test
    void testUpdateOfferedCourseToDatabase_CourseNotFound() {
        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidCourseIdException.class, () -> {
            offerredCourseService.updateOfferedCourseToDatabase(offeredCourseDTO, 1L);
        });
    }

    @Test
    void testUpdateOfferedCourseToDatabase_Success() {
        Courses mockCourse = new Courses();
        mockCourse.setCourseId(1L);
        when(coursesRepository.findById(1L)).thenReturn(Optional.of(mockCourse));

        Facilities mockFacility = new Facilities();
        mockFacility.setId(1L);
        when(facilititesRepository.findById(1L)).thenReturn(Optional.of(mockFacility));

        OfferedCourses mockOfferedCourses = new OfferedCourses();
        mockOfferedCourses.setOfferedCourseId(1L);
        mockOfferedCourses.setStartDate(localDate);
        mockOfferedCourses.setEndDate(localDate.plusDays(5));
        mockOfferedCourses.setNoOfSeats(10L);
        mockOfferedCourses.setStartTime(localDateTime);
        mockOfferedCourses.setEndTime(endTime);
        mockOfferedCourses.setIsAllDay(IsAllDay.YES);
        mockOfferedCourses.setAvailableForEnrollment(AvailableForEnrollment.YES);
        mockOfferedCourses.setCourses(mockCourse);
        mockOfferedCourses.setFacilities(mockFacility);

        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(mockOfferedCourses));

        offerredCourseService.updateOfferedCourseToDatabase(offeredCourseDTO, 1L);

        verify(offeredCourseRepository, times(1)).save(mockOfferedCourses);
        assertEquals(localDate, mockOfferedCourses.getStartDate());
        assertEquals(localDate.plusDays(5), mockOfferedCourses.getEndDate());
        assertEquals(10L, mockOfferedCourses.getNoOfSeats());
        assertEquals(localDateTime, mockOfferedCourses.getStartTime());
        assertEquals(endTime, mockOfferedCourses.getEndTime());
        assertEquals(IsAllDay.YES, mockOfferedCourses.getIsAllDay());
        assertEquals(AvailableForEnrollment.YES, mockOfferedCourses.getAvailableForEnrollment());
    }

    @Test
    void testGetOfferedCoursesById_Success() {
        Courses mockCourse = new Courses();
        mockCourse.setCourseId(1L);
        when(coursesRepository.findById(1L)).thenReturn(Optional.of(mockCourse));

        Facilities mockFacility = new Facilities();
        mockFacility.setId(1L);
        when(facilititesRepository.findById(1L)).thenReturn(Optional.of(mockFacility));
        OfferedCourses mockOfferedCourses = new OfferedCourses();
        mockOfferedCourses.setOfferedCourseId(1L);
        mockOfferedCourses.setStartDate(localDate);
        mockOfferedCourses.setEndDate(localDate.plusDays(5));
        mockOfferedCourses.setNoOfSeats(10L);
        mockOfferedCourses.setStartTime(localDateTime);
        mockOfferedCourses.setEndTime(endTime);
        mockOfferedCourses.setIsAllDay(IsAllDay.YES);
        mockOfferedCourses.setAvailableForEnrollment(AvailableForEnrollment.YES);
        mockOfferedCourses.setFacilities(mockFacility);
        mockOfferedCourses.setCourses(mockCourse);

        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(mockOfferedCourses));

        OfferedCourseDTO result = offerredCourseService.getOfferedCoursesById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCoursesId());
        assertEquals(localDate, result.getStartDate());
        assertEquals(localDateTime, result.getStartTime());
        assertEquals(IsAllDay.YES, result.getIsAllDay());
        assertEquals(AvailableForEnrollment.YES, result.getAvailableForEnrollment());
    }


}
