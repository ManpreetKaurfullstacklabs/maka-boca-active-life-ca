package io.reactivestax.activelife.distribution;

import io.reactivestax.activelife.domain.agegroup.AgeGroups;
import io.reactivestax.activelife.domain.course.Categories;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.course.Subcategories;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferedCourseSpecificationTest {

    @Mock
    private Root<OfferedCourses> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    @Mock
    private Path<Courses> coursePath;

    @Mock
    private Path<Subcategories> subCategoryPath;

    @Mock
    private Path<Categories> categoryPath;

    @Mock
    private Path<String> stringPath;
    @Mock
    private Path<?> coursesPath;

    @Mock
    private Path<?> subcategoriesPath;

    @Mock
    private Path<?> subcategoryNamePath;



    @Mock
    private Path<Facilities> facilityPath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
    }

    @Test
    void testWithCourseName() {
        Mockito.<Path<Courses>>when(root.get("courses")).thenReturn(coursePath);
        Mockito.<Path<String>>when(coursePath.get("courseName")).thenReturn(stringPath);
        when(criteriaBuilder.equal(stringPath, "Yoga")).thenReturn(predicate);

        Specification<OfferedCourses> spec = OfferedCourseSpecification.withCourseName("Yoga");
        assertNotNull(spec);

    }

    @Test
    void testWithCategoryName() {
        Mockito.<Path<Courses>>when(root.get("courses")).thenReturn(coursePath);
        Mockito.<Path<Subcategories>>when(coursePath.get("subCategories")).thenReturn(subCategoryPath);
        Mockito.<Path<Categories>>when(subCategoryPath.get("categories")).thenReturn(categoryPath);
        Mockito.<Path<String>>when(categoryPath.get("name")).thenReturn(stringPath);
        when(criteriaBuilder.equal(stringPath, "Fitness")).thenReturn(predicate);

        Specification<OfferedCourses> spec = OfferedCourseSpecification.hasCategory("Fitness");
        assertNotNull(spec);

    }

    @Test
    void testWithSubCategoryName() {
        Mockito.<Path<Courses>>when(root.get("courses")).thenReturn(coursePath);
        Mockito.<Path<Subcategories>>when(coursePath.get("subcategories")).thenReturn(subCategoryPath);
        Mockito.<Path<String>>when(subCategoryPath.get("name")).thenReturn(stringPath);
        when(criteriaBuilder.equal(stringPath, "Wellness")).thenReturn(predicate);
        Specification<OfferedCourses> spec = OfferedCourseSpecification.hasSubCategory("Wellness");
        assertNotNull(spec);
    }




    @Test
    void testWithStartDate() {
        Path<?> startDatePath = mock(Path.class);
        when(root.get("startDate")).thenReturn((Path) startDatePath);
        when(criteriaBuilder.equal((Expression) startDatePath, LocalDate.of(2025, 1, 1))).thenReturn(predicate);
        Specification<OfferedCourses> spec = OfferedCourseSpecification.withStartDate(LocalDate.of(2025, 1, 1));
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertNotNull(resultPredicate);
    }

    @Test
    void testWithEndDate() {
        Path<?> endDatePath = mock(Path.class);

        when(root.get("endDate")).thenReturn((Path) endDatePath);
        when(criteriaBuilder.equal((Expression) endDatePath, LocalDate.of(2025, 12, 31))).thenReturn(predicate);
        Specification<OfferedCourses> spec = OfferedCourseSpecification.withEndDate(LocalDate.of(2025, 12, 31));
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertNotNull(resultPredicate);
    }

    @Test
    void testWithCity() {
        Mockito.<Path<Facilities>>when(root.get("facility")).thenReturn(facilityPath);
        Mockito.<Path<String>>when(facilityPath.get("city")).thenReturn(stringPath);
        when(criteriaBuilder.equal(stringPath, "Toronto")).thenReturn(predicate);

        Specification<OfferedCourses> spec = OfferedCourseSpecification.withCity("Toronto");
        assertNotNull(spec);
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithProvince() {
        Mockito.<Path<Facilities>>when(root.get("facility")).thenReturn(facilityPath);
        Mockito.<Path<String>>when(facilityPath.get("province")).thenReturn(stringPath);
        when(criteriaBuilder.equal(stringPath, "Ontario")).thenReturn(predicate);

        Specification<OfferedCourses> spec = OfferedCourseSpecification.withProvince("Ontario");
        assertNotNull(spec);
        assertNotNull(spec.toPredicate(root, query, criteriaBuilder));
    }

    @Test
    void testWithCourseNameNull() {
        Specification<OfferedCourses> spec = OfferedCourseSpecification.withCourseName(null);
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertEquals(criteriaBuilder.conjunction(), resultPredicate);
    }

    @Test
    void testWithCourseNameEmpty() {
        Specification<OfferedCourses> spec = OfferedCourseSpecification.withCourseName("");
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertEquals(criteriaBuilder.conjunction(), resultPredicate);
    }

    @Test
    void testWithStartDateNull() {
        Specification<OfferedCourses> spec = OfferedCourseSpecification.withStartDate(null);
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertEquals(criteriaBuilder.conjunction(), resultPredicate);
    }

    @Test
    void testWithEndDateNull() {
        Specification<OfferedCourses> spec = OfferedCourseSpecification.withEndDate(null);
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertEquals(criteriaBuilder.conjunction(), resultPredicate);
    }

    @Test
    void testGetWildcardSearch() {
        String search = "Yoga";
        String expected = "%yoga%";
        String result = OfferedCourseSpecification.getWildcardSearch(search);
        assertEquals(expected, result);
    }

    @Test
    void testWithCityNoMatch() {
        Mockito.<Path<Facilities>>when(root.get("facility")).thenReturn(facilityPath);
        Mockito.<Path<String>>when(facilityPath.get("city")).thenReturn(stringPath);
        when(criteriaBuilder.like(stringPath, "%Toronto%")).thenReturn(predicate);

        Specification<OfferedCourses> spec = OfferedCourseSpecification.withCity("NonExistentCity");
        assertNotNull(spec);
        Predicate resultPredicate = spec.toPredicate(root, query, criteriaBuilder);
        assertEquals(criteriaBuilder.conjunction(), resultPredicate);
    }


}
