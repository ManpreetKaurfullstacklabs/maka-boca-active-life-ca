package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;
import io.reactivestax.activelife.utility.interfaces.OfferedCourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchQueryService {

    @Autowired
    OfferedCourseRepository offeredCourseRepository;

    @Autowired
    OfferedCourseSpecification offeredCourseSpecification;


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
