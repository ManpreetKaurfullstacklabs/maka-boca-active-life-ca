package io.reactivestax.activelife.criteriabuilder;

import io.reactivestax.activelife.domain.course.OfferedCourses;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OfferedCourseSpecification {

    public static Specification<OfferedCourses> withCourseName(String courseName) {
        return (root, query, criteriaBuilder) ->
                courseName == null || courseName.trim().isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("courses").get("name")),
                        getWildcardSearch(courseName)
                );
    }


    public static Specification<OfferedCourses> withStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("startDate"), startDate);
    }

    public static Specification<OfferedCourses> withEndDate(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.get("endDate"), endDate);
    }




    public static String getWildcardSearch(String search) {
        return "%" + search.trim().toLowerCase() + "%";
    }

    public static String getWildcardDateSearch(LocalDate search) {
        return "%" + search+ "%";
    }

}
