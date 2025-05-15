package io.reactivestax.activelife.utility.interfaces;

import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.dto.CourseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDTO toDTO(Courses courses);
}
