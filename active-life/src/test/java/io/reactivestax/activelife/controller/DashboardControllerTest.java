package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.SearchQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DashboardControllerTest {

    @InjectMocks
    private Dashboard dashboardController;

    @Mock
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @Mock
    private SearchQueryService searchQueryService;

    private Courses mockCourse;
    private List<Courses> mockCoursesList;
    private OfferedCouseSearchRequestDTO requestDTO;
    private OfferedCourseDTO offeredCourseDTO;
    private List<OfferedCourseDTO> mockResponseList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCourse = new Courses();
        mockCourse.setCourseId(1L);
        mockCourse.setName("Cardio Blast");
        mockCourse.setDescription("Intense cardio workout");

        mockCoursesList = Arrays.asList(mockCourse);

        requestDTO = new OfferedCouseSearchRequestDTO();
        requestDTO.setCity("Los Angeles");

        offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setCoursesId(1L);
        offeredCourseDTO.setNoOfSeats(10L);

        mockResponseList = Arrays.asList(offeredCourseDTO);
    }

    @Test
    void testGetCoursesForWaitlistedMembers() {
        when(familyCourseRegistrationService.getCoursesForWaitlistedMembers()).thenReturn(mockCoursesList);
        ResponseEntity<List<Courses>> response = dashboardController.getCoursesForWaitlistedMembers(1L, "YES");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Cardio Blast", response.getBody().get(0).getName());
        verify(familyCourseRegistrationService, times(1)).getCoursesForWaitlistedMembers();
    }

    @Test
    void testSearchOfferedCourses() {
        when(searchQueryService.searchOfferedCourse(any(OfferedCouseSearchRequestDTO.class))).thenReturn(mockResponseList);

        List<OfferedCourseDTO> response = dashboardController.searchOfferedCourses(requestDTO);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getCoursesId());
        assertEquals(10, response.get(0).getNoOfSeats());
        verify(searchQueryService, times(1)).searchOfferedCourse(any(OfferedCouseSearchRequestDTO.class));
    }
}
