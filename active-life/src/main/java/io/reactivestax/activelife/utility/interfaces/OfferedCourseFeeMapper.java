package io.reactivestax.activelife.utility.interfaces;

import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.dto.OfferedCourseFeeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferedCourseFeeMapper {
    OfferedCourseFeeDTO toDTO(OfferedCourseFee fee);
}
