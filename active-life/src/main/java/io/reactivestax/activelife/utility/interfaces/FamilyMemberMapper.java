package io.reactivestax.activelife.utility.interfaces;

import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FamilyMemberMapper {

    FamilyMemberMapper INSTANCE = Mappers.getMapper(FamilyMemberMapper.class);
    @Mapping(source = "familyGroupId.familyGroupId", target = "familyGroupId")
    MemberRegistrationDTO toDto(MemberRegistration memberRegistration);

    @Mapping(source = "familyGroupId", target = "familyGroupId.familyGroupId")
    MemberRegistration toEntity(MemberRegistrationDTO memberRegistrationDTO);
}
