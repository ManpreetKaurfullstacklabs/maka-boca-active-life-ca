package io.reactivestax.activelife.utility.interfaces;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OfferedCourseMapper {

    OfferedCourseMapper INSTANCE = Mappers.getMapper(OfferedCourseMapper.class);
    @Mapping(source = "facilities.id", target = "facilities")
    OfferedCourseDTO offeredCourseToOfferedCourseDTO(OfferedCourses offeredCourses);
    default Long map(Facilities facilities) {
        return facilities != null ? facilities.getId() : null;
    }
}
