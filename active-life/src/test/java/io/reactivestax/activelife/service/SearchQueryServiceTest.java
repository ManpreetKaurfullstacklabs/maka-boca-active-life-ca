//package io.reactivestax.activelife.service;
//
//import io.reactivestax.activelife.domain.course.OfferedCourses;
//import io.reactivestax.activelife.dto.OfferedCourseDTO;
//import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
//import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
//import io.reactivestax.activelife.service.SearchQueryService;
//import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;
//import io.reactivestax.activelife.utility.interfaces.OfferedCourseMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class SearchQueryServiceTest {
//
//    @Mock
//    private OfferedCourseRepository offeredCourseRepository;
//
//    @Mock
//    private OfferedCourseMapper offeredCourseMapper;
//
//    @InjectMocks
//    private SearchQueryService searchQueryService;
//
//    @Test
//    void searchOfferedCourseTest() {
//
//        OfferedCouseSearchRequestDTO searchRequest = new OfferedCouseSearchRequestDTO();
//        searchRequest.setCourseName("Course Name");
//        searchRequest.setStartDate(null);
//        searchRequest.setEndDate(null);
//        searchRequest.setCity("New York");
//        searchRequest.setProvince("NY");
//        searchRequest.setCategoryName("Category1");
//        searchRequest.setSubCategory("SubCategory1");
//
//        OfferedCourses course1 = new OfferedCourses();
//        course1.setOfferedCourseId(1L);
//        course1.setStartDate(null);
//        course1.setEndDate(null);
//
//        OfferedCourses course2 = new OfferedCourses();
//        course2.setOfferedCourseId(2L);
//        course2.setStartDate(null);
//        course2.setEndDate(null);
//
//        List<OfferedCourses> mockCourses = Arrays.asList(course1, course2);
//
//        when(offeredCourseRepository.findAll(org.mockito.Mockito.any(Specification.class))).thenReturn(mockCourses);
//        OfferedCourseDTO dto1 = new OfferedCourseDTO();
//        OfferedCourseDTO dto2 = new OfferedCourseDTO();
//        List<OfferedCourseDTO> result = searchQueryService.searchOfferedCourse(searchRequest);
//
//        assertEquals(2, result.size());
//        assertEquals(dto1, result.get(0));
//        assertEquals(dto2, result.get(1));
//    }
//}
