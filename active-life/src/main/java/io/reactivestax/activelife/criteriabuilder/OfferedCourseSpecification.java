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

    public static Specification<OfferedCourses> withCity(String cityName) {
        return (root, query, criteriaBuilder) ->
                cityName == null || cityName.trim().isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("facilities").get("city")),
                        getWildcardSearch(cityName)
                );
    }
    public static Specification<OfferedCourses> withProvince(String province) {
        return (root, query, criteriaBuilder) ->
                province == null || province.trim().isEmpty()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("facilities").get("province")),
                        getWildcardSearch(province)
                );
    }
    public static Specification<OfferedCourses> hasCategory(String category) {
        return ((root, query, criteriaBuilder) -> category == null || category.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get("courses").get("subcategories")
                        .get("categories").get("name")),
                getWildcardSearch(category)
        ));
    }
    public static Specification<OfferedCourses> hasSubCategory(String subCategory) {
        return ((root, query, criteriaBuilder) -> subCategory == null || subCategory.isEmpty()
                ? criteriaBuilder.conjunction() : criteriaBuilder.like(
                criteriaBuilder.lower(root.get("courses").get("subcategories").get("name")),
                getWildcardSearch(subCategory)
        ));
    }




    public static String getWildcardSearch(String search) {
        return "%" + search.trim().toLowerCase() + "%";
    }

    public static String getWildcardDateSearch(LocalDate search) {
        return "%" + search+ "%";
    }

}
