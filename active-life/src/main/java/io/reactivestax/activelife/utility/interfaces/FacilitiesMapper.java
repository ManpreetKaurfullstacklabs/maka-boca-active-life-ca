package io.reactivestax.activelife.utility.interfaces;

import io.reactivestax.activelife.domain.facility.Facilities;
import io.reactivestax.activelife.dto.FacilititesDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilitiesMapper {
    FacilititesDTO toDTO(Facilities facilities);
}
