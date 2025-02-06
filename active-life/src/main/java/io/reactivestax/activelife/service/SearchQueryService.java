package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCouseSearchRequestDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.utility.criteriabuilder.OfferedCourseSpecification;
import io.reactivestax.activelife.utility.interfaces.OfferedCourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchQueryService {

    private final OfferedCourseRepository offeredCourseRepository;

    public List<OfferedCourseDTO> searchOfferedCourse(OfferedCouseSearchRequestDTO offeredCouseSearchRequestDTO) {
        Specification<OfferedCourses> offeredCoursesSpecification =
                Specification.where(OfferedCourseSpecification.withCourseName(offeredCouseSearchRequestDTO.getCourseName())
                        .and(OfferedCourseSpecification.withStartDate(offeredCouseSearchRequestDTO.getStartDate()))
                        .and(OfferedCourseSpecification.withEndDate(offeredCouseSearchRequestDTO.getEndDate()))
                        .and(OfferedCourseSpecification.withCity(offeredCouseSearchRequestDTO.getCity()))
                        .and(OfferedCourseSpecification.withProvince(offeredCouseSearchRequestDTO.getProvince()))
                        .and(OfferedCourseSpecification.hasCategory(offeredCouseSearchRequestDTO.getCategoryName()))
                        .and(OfferedCourseSpecification.hasSubCategory(offeredCouseSearchRequestDTO.getSubCategory())));


        List<OfferedCourses> offeredCoursesList = offeredCourseRepository.findAll(offeredCoursesSpecification);
        List<OfferedCourseDTO> collect = offeredCoursesList.stream()
                .map(OfferedCourseMapper.INSTANCE::offeredCourseToOfferedCourseDTO)
                .collect(Collectors.toList());
        return collect;
    }

}
