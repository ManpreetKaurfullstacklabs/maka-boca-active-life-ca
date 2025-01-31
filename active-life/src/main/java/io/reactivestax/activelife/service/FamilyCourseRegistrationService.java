package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.exception.CourseNotFoundException;
import io.reactivestax.activelife.exception.MemberNotFoundException;
import io.reactivestax.activelife.repository.OfferedCourseRepository;
import io.reactivestax.activelife.repository.familymemberrepositries.FamilMemberRepository;
import io.reactivestax.activelife.repository.familymemberrepositries.FamilyCourseRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class FamilyCourseRegistrationService {

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private FamilMemberRepository familMemberRepository;

    @Transactional
    public void addfamilyMemberToCourse(FamilyCourseRegistrationDTO familyCourseRegistrationDTO){
        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setCost(familyCourseRegistrationDTO.getCost());
        familyCourseRegistrations.setEnrollmentDate(familyCourseRegistrationDTO.getEnrollmentDate());
        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.NO);
        familyCourseRegistrations.setWithdrawnCredits(0L);
        familyCourseRegistrations.setEnrollmentActor(familyCourseRegistrationDTO.getEnrollmentActor());
        familyCourseRegistrations.setEnrollmentActorId(familyCourseRegistrationDTO.getEnrollmentActorId());
        familyCourseRegistrations.setCreatedAt(familyCourseRegistrationDTO.getCreatedAt());
        familyCourseRegistrations.setOfferedCourseId(offeredCourseExistsOrNot(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setFamilyMemberId(memberIsActiveOrNot(familyCourseRegistrationDTO.getFamilyMemberId()));
        familyCourseRegistrations.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        familyCourseRegistrations.setCreatedBy(familyCourseRegistrationDTO.getCreatedBy());
        familyCourseRegistrations.setLastUpdateBy(familyCourseRegistrationDTO.getLastUpdateBy());
        familyCourseRegistrationRepository.save(familyCourseRegistrations);
    }
    public OfferedCourses offeredCourseExistsOrNot(Long courseId){
        Optional<OfferedCourses> byId = offeredCourseRepository.findById(courseId);
        OfferedCourses offeredCourses = byId.get();
        if(offeredCourses.getOfferedCourseId().equals(courseId)){
            return offeredCourses;
        }
        throw new CourseNotFoundException("course not found");
    }

    public FamilyMembers memberIsActiveOrNot(Long id){
        Optional<FamilyMembers> byId = familMemberRepository.findById(id);
        FamilyMembers familyMembers = byId.get();
        if(familyMembers.getStatus().equals(Status.INACTIVE)){
            return familyMembers;
        }
        throw new MemberNotFoundException("Cannot Assign this member is inactive ");
    }
 public FamilyCourseRegistrationDTO getAllFamilyMemberRegistration(Long id){
        FamilyCourseRegistrationDTO familyCourseRegistrationDTO = new FamilyCourseRegistrationDTO();
     Optional<FamilyCourseRegistrations> byId = familyCourseRegistrationRepository.findById(id);
     FamilyCourseRegistrations familyCourseRegistrations = byId.get();
     familyCourseRegistrationDTO.setCost(familyCourseRegistrations.getCost());
     FamilyMembers member = memberIsEnrolledOrNot(familyCourseRegistrations.getFamilyMemberId().getFamilyMemberId());
     familyCourseRegistrationDTO.setFamilyMemberId(member.getFamilyMemberId());
     familyCourseRegistrationDTO.setEnrollmentDate(familyCourseRegistrations.getEnrollmentDate());
     familyCourseRegistrationDTO.setWithdrawnCredits(familyCourseRegistrations.getWithdrawnCredits());
     familyCourseRegistrationDTO.setEnrollmentActorId(familyCourseRegistrations.getEnrollmentActorId());
     familyCourseRegistrationDTO.setCreatedAt(familyCourseRegistrations.getCreatedAt());
     OfferedCourses offeredCourses = offeredCourseExistsOrNot(familyCourseRegistrations.getOfferedCourseId().getOfferedCourseId());
     familyCourseRegistrationDTO.setOfferedCourseId(offeredCourses.getOfferedCourseId());
     familyCourseRegistrationDTO.setCreatedBy(familyCourseRegistrations.getCreatedBy());
     familyCourseRegistrationDTO.setLastUpdatedTime(familyCourseRegistrations.getLastUpdatedTime());
     familyCourseRegistrationDTO.setLastUpdateBy(familyCourseRegistrations.getLastUpdateBy());
     familyCourseRegistrationDTO.setIsWithdrawn(familyCourseRegistrations.getIsWithdrawn());
     familyCourseRegistrationDTO.setEnrollmentActor(familyCourseRegistrations.getEnrollmentActor());
     familyCourseRegistrations.setWithdrawnCredits(familyCourseRegistrations.getWithdrawnCredits());

     return familyCourseRegistrationDTO;
 }
    public FamilyMembers memberIsEnrolledOrNot(Long id){
        Optional<FamilyMembers> byId = familMemberRepository.findById(id);
        FamilyMembers familyMembers = byId.get();
        if(familyMembers.getStatus().equals(Status.INACTIVE)){
            return familyMembers ;
        }
        throw new MemberNotFoundException("Cannot Assign this member is inactive ");
    }

    public void deleteFamilyMemeberFromRegisteredCourse(Long id){
        Optional<FamilyCourseRegistrations> familyCourseRegistrationRepositorybyId = familyCourseRegistrationRepository.findById(id);
        if(familyCourseRegistrationRepositorybyId.isEmpty()){
            throw  new MemberNotFoundException("member is not enrolled in any course");
        }
        FamilyCourseRegistrations familyCourseRegistrations = familyCourseRegistrationRepositorybyId.get();
        if(familyCourseRegistrations.getIsWithdrawn().equals(IsWithdrawn.YES)){
            throw new RuntimeException("Member already withdrawn from course");
        }
        if(familyCourseRegistrations.getIsWithdrawn().equals(IsWithdrawn.NO)){
            familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.YES);
            familyCourseRegistrationRepository.save(familyCourseRegistrations);
        }
    }


}
