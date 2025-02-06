//package io.reactivestax.activelife.utility.criteriabuilder;
//
//import io.reactivestax.activelife.domain.course.OfferedCourses;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Root;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//
//import java.time.LocalDate;
//
//import static org.mockito.Mockito.*;
//
//class OfferedCourseSpecificationTest {
//
//    private CriteriaBuilder criteriaBuilder;
//    private CriteriaQuery<OfferedCourses> criteriaQuery;
//    private Root<OfferedCourses> root;
//
//    @BeforeEach
//    void setUp() {
//        // Mock CriteriaBuilder, CriteriaQuery, and Root
//        criteriaBuilder = mock(CriteriaBuilder.class);
//        criteriaQuery = mock(CriteriaQuery.class);
//        root = mock(Root.class);
//    }
//
//    @Test
//    void testWithCourseName() {
//        // Given a specification with course name
//        String courseName = "Yoga";
//        OfferedCourseSpecification.withCourseName(courseName).toPredicate(root, criteriaQuery, criteriaBuilder);
//
//        // Verify that the necessary methods were called on the mock objects
//        verify(criteriaBuilder).like(any(), anyString());
//        verify(criteriaBuilder).lower(any());
//    }
//
//    @Test
//    void testWithStartDate() {
//
//        LocalDate startDate = LocalDate.of(2025, 2, 9);
//        OfferedCourseSpecification.withStartDate(startDate).toPredicate(root, criteriaQuery, criteriaBuilder);
//
//        verify(criteriaBuilder).equal(any(), any());
//    }
//
//    @Test
//    void testWithCity() {
//        // Given a specification with city
//        String cityName = "Dallas";
//        OfferedCourseSpecification.withCity(cityName).toPredicate(root, criteriaQuery, criteriaBuilder);
//
//        // Verify that the necessary methods were called on the mock objects
//        verify(criteriaBuilder).like(any(), anyString());
//        verify(criteriaBuilder).lower(any());
//    }
//
//    @Test
//    void testHasCategory() {
//        // Given a specification with category
//        String category = "Fitness";
//        OfferedCourseSpecification.hasCategory(category).toPredicate(root, criteriaQuery, criteriaBuilder);
//        // Verify that the necessary methods were called on the mock objects
//        verify(criteriaBuilder).like(any(), anyString());
//        verify(criteriaBuilder).lower(any());
//    }
//}
