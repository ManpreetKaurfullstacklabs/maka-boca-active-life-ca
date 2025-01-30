//package io.reactivestax.activelife.service;
//
//import io.reactivestax.activelife.Enums.Status;
//import io.reactivestax.activelife.domain.membership.FamilyGroups;
//import io.reactivestax.activelife.domain.membership.FamilyMembers;
//import io.reactivestax.activelife.dto.FamilyMemberDTO;
//import io.reactivestax.activelife.repository.FamilyGroupRepository;
//import org.hibernate.annotations.SecondaryRows;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Service
//public class FamilyGroupService {
//
//    private FamilyGroupRepository familyGroupRepository;
//
//    public FamilyGroups createNewFamilyGroup(FamilyMemberDTO familyMemberDTO){
//        FamilyGroups familyGroups = new FamilyGroups();
//        familyGroups.setFamilyPin();
//        familyGroups.setStatus(Status.INACTIVE);
//        familyGroups.setCredits(new BigDecimal(0));
//        familyGroups.setCreatedAt(LocalDateTime.now());
//        familyGroups.setUpdatedAt(LocalDateTime.now());
//        familyGroups.setCreatedBy(1L);
//        familyGroups.setLastUpdatedBy(1L);
//        familyGroupRepository.save(familyGroups);
//        return familyGroups;
//    }
//}
