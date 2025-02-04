package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.repository.courses.CoursesRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.facilities.FacilititesRepository;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OfferredCourseServiceTest {

    @Mock
    private OfferedCourseRepository offeredCourseRepository;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private FacilititesRepository facilititesRepository;

    @Mock
    private OfferedCourseSpecification offeredCourseSpecification;

    @InjectMocks
    private OfferredCourseService offerredCourseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOfferedCourseToDatabase() {
        // Arrange
        OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setCoursesId(1L);
        offeredCourseDTO.setStartDate(LocalDate.now());
        offeredCourseDTO.setEndDate(LocalDate.now().plusMonths(1));
        offeredCourseDTO.setNoOfSeats(30L);

        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();

        // Mock dependencies
        Courses mockCourse = new Courses();
        mockCourse.setCourseId(1L);

        Facilities mockFacility = new Facilities();
        mockFacility.setId(1L);

        OfferedCourses mockOfferedCourse = new OfferedCourses();
        mockOfferedCourse.setOfferedCourseId(1L);
        mockOfferedCourse.setCourses(mockCourse);
        mockOfferedCourse.setFacilities(mockFacility);


        when(coursesRepository.findById(1L)).thenReturn(java.util.Optional.of(mockCourse));
        when(facilititesRepository.findById(1L)).thenReturn(java.util.Optional.of(mockFacility));
        when(offeredCourseRepository.save(any(OfferedCourses.class))).thenReturn(mockOfferedCourse);

        // Act
        offerredCourseService.addOfferedCourseToDatabase(offeredCourseDTO);

        // Assert
        verify(offeredCourseRepository, times(1)).save(any(OfferedCourses.class));
    }

    @Test
    void testGetOfferedCoursesById() {
        // Arrange
        Long courseId = 1L;
        OfferedCourses mockOfferedCourse = new OfferedCourses();
        mockOfferedCourse.setOfferedCourseId(courseId);
        mockOfferedCourse.setStartDate(LocalDate.now());
        mockOfferedCourse.setEndDate(LocalDate.now().plusMonths(1));
        mockOfferedCourse.setNoOfClasses(30L);

        when(offeredCourseRepository.findById(courseId)).thenReturn(java.util.Optional.of(mockOfferedCourse));

        // Act
        OfferedCourseDTO result = offerredCourseService.getOfferedCoursesById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getCoursesId());
        assertEquals(30L, result.getNoOfSeats());
    }

    @Test
    void testUpdateOfferedCourseToDatabase() {
        // Arrange
        Long courseId = 1L;
        OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setStartDate(LocalDate.now());
        offeredCourseDTO.setEndDate(LocalDate.now().plusMonths(1));
        offeredCourseDTO.setNoOfSeats(30L);

        OfferedCourses existingCourse = new OfferedCourses();
        existingCourse.setOfferedCourseId(courseId);
        existingCourse.setCourses(new Courses());
        existingCourse.setFacilities(new Facilities());

        when(offeredCourseRepository.findById(courseId)).thenReturn(java.util.Optional.of(existingCourse));
        when(offeredCourseRepository.save(any(OfferedCourses.class))).thenReturn(existingCourse);

        // Act
        offerredCourseService.updateOfferedCourseToDatabase(offeredCourseDTO, courseId);

        // Assert
        verify(offeredCourseRepository, times(1)).save(any(OfferedCourses.class));
    }

    @Test
    void testUpdateOfferedCourseToDatabase_shouldThrowExceptionIfCourseNotFound() {
        // Arrange
        Long courseId = 1L;
        OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();
        when(offeredCourseRepository.findById(courseId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(InvalidCourseIdException.class, () -> {
            offerredCourseService.updateOfferedCourseToDatabase(offeredCourseDTO, courseId);
        });
    }


}
