package io.reactivestax.activelife.utility.interfaces;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        uses = {
                CourseMapper.class,
                FacilitiesMapper.class,
                OfferedCourseFeeMapper.class
        }
)
public interface OfferedCourseMapper {

    OfferedCourseMapper INSTANCE = Mappers.getMapper(OfferedCourseMapper.class);

    @Mappings({
            @Mapping(source = "facilities.id", target = "facilities"),
            @Mapping(source = "facilities", target = "facilititesDTO"),
            @Mapping(source = "courses.courseId", target = "coursesId"),
            @Mapping(source = "courses", target = "courseDTO"),
            @Mapping(source = "offeredCourseFee", target = "offeredCourseFeeDTO")
    })
    OfferedCourseDTO offeredCourseToOfferedCourseDTO(OfferedCourses offeredCourses);
}
