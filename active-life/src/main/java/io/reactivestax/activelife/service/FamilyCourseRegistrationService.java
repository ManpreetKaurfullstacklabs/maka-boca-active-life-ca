package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import io.reactivestax.activelife.utility.distribution.SmsService;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.domain.course.WaitList;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FamilyCourseRegistrationService {

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private MemberRegistrationRepository memberRegistrationRepository;

    @Autowired
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private SmsService smsService;

    private static final int MAX_WAITLIST_SIZE = 5;

    @Transactional
    public void enrollFamilyMemberInCourse(FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        OfferedCourses offeredCourse = getOfferedCourse(familyCourseRegistrationDTO.getOfferedCourseId());
        Long availableSeats = offeredCourse.getNoOfSeats();
        Long enrolledCount = familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO);
        FamilyMembers familyMember = getFamilyMember(familyCourseRegistrationDTO.getFamilyMemberId());
        Optional<FamilyCourseRegistrations> existingRegistration = familyCourseRegistrationRepository
                .findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse);

        if (existingRegistration.isPresent()) {
            throw new RuntimeException("Family member is already enrolled in this course.");
        }

        if (enrolledCount < availableSeats) {
            enrollMember(familyCourseRegistrationDTO, IsWaitListed.NO);
        } else {
            handleWaitlist(familyCourseRegistrationDTO.getFamilyMemberId(), familyCourseRegistrationDTO.getOfferedCourseId());

        }
    }

    private void handleWaitlist(Long familyMemberId, Long offeredCourseId) {
        Long waitlistCount = waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourseId, IsWaitListed.YES);
        if (waitlistCount < MAX_WAITLIST_SIZE) {
            addToWaitlist(familyMemberId, offeredCourseId);
        } else {
            throw new RuntimeException("Waitlist is full for this course.");
        }

    }

    public String addToWaitlist(Long familyMemberId, Long offeredCourseId) {
        FamilyMembers familyMember = getFamilyMember(familyMemberId);
        OfferedCourses offeredCourse = getOfferedCourse(offeredCourseId);
        WaitList waitList = new WaitList();
        waitList.setFamilyMember(familyMember);
        waitList.setOfferedCourses(offeredCourse);
        waitList.setNoOfSeats(1L);
        waitList.setIsWaitListed(IsWaitListed.YES);
        waitlistRepository.save(waitList);
        return "Course Seats full adding to waitlist";
    }


    @Transactional
    private void enrollMember(FamilyCourseRegistrationDTO familyCourseRegistrationDTO, IsWaitListed waitListed) {
        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setFamilyMemberId(getFamilyMember(familyCourseRegistrationDTO.getFamilyMemberId()));
        familyCourseRegistrations.setOfferedCourseId(getOfferedCourse(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setCost(getCostOfferedFromCourses(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setEnrollmentDate(familyCourseRegistrationDTO.getEnrollmentDate());
        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.NO);
        familyCourseRegistrations.setWithdrawnCredits(familyCourseRegistrationDTO.getWithdrawnCredits());
        familyCourseRegistrations.setNoOfseats(getNoOfSeatsFromOfferedCoursesTable(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setEnrollmentActorId(familyCourseRegistrationDTO.getFamilyMemberId());
        familyCourseRegistrations.setCreatedAt(familyCourseRegistrationDTO.getCreatedAt());
        familyCourseRegistrations.setOfferedCourseId(getOfferedCourse(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setIsWaitListed(waitListed);
        familyCourseRegistrations.setFamilyMemberId(getFamilyMember(familyCourseRegistrationDTO.getFamilyMemberId()));
        familyCourseRegistrations.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        familyCourseRegistrations.setCreatedBy(familyCourseRegistrationDTO.getCreatedBy());
        familyCourseRegistrations.setLastUpdateBy(familyCourseRegistrationDTO.getLastUpdateBy());

        familyCourseRegistrationRepository.save(familyCourseRegistrations);
    }

    private OfferedCourses getOfferedCourse(Long offeredCourseId) {
        Optional<OfferedCourses> offeredCourseOpt = offeredCourseRepository.findById(offeredCourseId);
        if (offeredCourseOpt.isEmpty()) {
            throw new InvalidCourseIdException("Offered course does not exist.");
        }
        OfferedCourses offeredCourse = offeredCourseOpt.get();
        if (offeredCourse.getAvailableForEnrollment().equals(AvailableForEnrollment.NO)) {
            throw new RuntimeException("Enrollment is not available for this course.");
        }
        return offeredCourse;
    }

    public FamilyMembers getFamilyMember(Long familyMemberId) {
        Optional<FamilyMembers> byId = memberRegistrationRepository.findById(familyMemberId);
        if (byId.isEmpty()) {
            throw new InvalidMemberIdException("Family member does not exist.");
        }
        FamilyMembers familyMembers = byId.get();
        if (familyMembers.getStatus().equals(Status.INACTIVE)) {
            throw new RuntimeException("Enrollment is not available for this member because it's inactive.");
        }
        return familyMembers;
    }


    public Long getNoOfSeatsFromOfferedCoursesTable(Long id) {
        Optional<OfferedCourses> byId = offeredCourseRepository.findById(id);
        if (byId.isEmpty()) {
            throw new InvalidCourseIdException("Course does not exist.");
        }
        return byId.get().getNoOfSeats();
    }

    public Long getCostOfferedFromCourses(Long id) {
        OfferedCourses offeredCourses = offeredCourseExistsOrNot(id);
        Long offeredCourseId = offeredCourses.getOfferedCourseId();
        Optional<OfferedCourseFee> feeOpt = offeredCourseFeeRepository.findById(offeredCourseId);
        OfferedCourseFee offeredCourseFee = feeOpt.orElseThrow(() -> new RuntimeException("Course fee not found."));
        return offeredCourseFee.getCourseFee();
    }

    public OfferedCourses offeredCourseExistsOrNot(Long courseId) {
        Optional<OfferedCourses> byId = offeredCourseRepository.findById(courseId);
        if (byId.isEmpty()) {
            throw new InvalidCourseIdException("Course not found.");
        }
        return byId.get();
    }

    public FamilyMembers memberIsActiveOrNot(Long id) {
        Optional<FamilyMembers> byId = memberRegistrationRepository.findById(id);
        FamilyMembers familyMembers = byId.orElseThrow(() -> new InvalidMemberIdException("Member not found."));
        if (familyMembers.getStatus().equals(Status.INACTIVE)) {
            throw new InvalidMemberIdException("Member is inactive.");
        }
        return familyMembers;
    }

    public FamilyCourseRegistrationDTO getAllFamilyMemberRegistration(Long id) {
        Optional<FamilyCourseRegistrations> byId = familyCourseRegistrationRepository.findById(id);
        if (byId.isEmpty()) {
            throw new InvalidMemberIdException("Family course registration not found.");
        }
        FamilyCourseRegistrations familyCourseRegistrations = byId.get();
        FamilyCourseRegistrationDTO familyCourseRegistrationDTO = new FamilyCourseRegistrationDTO();
        FamilyMembers member = memberIsEnrolledOrNot(familyCourseRegistrations.getFamilyMemberId().getFamilyMemberId());
        familyCourseRegistrationDTO.setFamilyMemberId(member.getFamilyMemberId());
        familyCourseRegistrationDTO.setEnrollmentDate(familyCourseRegistrations.getEnrollmentDate());
        familyCourseRegistrationDTO.setWithdrawnCredits(familyCourseRegistrations.getWithdrawnCredits());
        familyCourseRegistrationDTO.setCreatedAt(familyCourseRegistrations.getCreatedAt());
        OfferedCourses offeredCourses = offeredCourseExistsOrNot(familyCourseRegistrations.getOfferedCourseId().getOfferedCourseId());
        familyCourseRegistrationDTO.setOfferedCourseId(offeredCourses.getOfferedCourseId());
        familyCourseRegistrationDTO.setCreatedBy(familyCourseRegistrations.getCreatedBy());
        familyCourseRegistrationDTO.setLastUpdatedTime(familyCourseRegistrations.getLastUpdatedTime());
        familyCourseRegistrationDTO.setLastUpdateBy(familyCourseRegistrations.getLastUpdateBy());
        familyCourseRegistrationDTO.setIsWithdrawn(familyCourseRegistrations.getIsWithdrawn());

        return familyCourseRegistrationDTO;
    }

    public FamilyMembers memberIsEnrolledOrNot(Long id) {
        Optional<FamilyMembers> byId = memberRegistrationRepository.findById(id);
        FamilyMembers familyMembers = byId.orElseThrow(() -> new InvalidMemberIdException("Member not found."));
        if (familyMembers.getStatus().equals(Status.INACTIVE)) {
            throw new InvalidMemberIdException("Member is inactive.");
        }
        return familyMembers;
    }

    public void deleteFamilyMemberFromRegisteredCourse(Long id) {
        Optional<FamilyCourseRegistrations> registrationOpt = familyCourseRegistrationRepository.findById(id);
        if (registrationOpt.isEmpty()) {
            throw new InvalidMemberIdException("Member is not enrolled in any course.");
        }
        FamilyCourseRegistrations familyCourseRegistrations = registrationOpt.get();

        if (familyCourseRegistrations.getIsWithdrawn().equals(IsWithdrawn.YES)) {
            throw new RuntimeException("Member already withdrawn from the course.");
        }

        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.YES);
        familyCourseRegistrationRepository.save(familyCourseRegistrations);

        OfferedCourses offeredCourse = familyCourseRegistrations.getOfferedCourseId();

        Long waitlistCount = waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourse.getOfferedCourseId(), IsWaitListed.YES);

        if (waitlistCount > 0) {
            notifyAllWaitlistedMembers(offeredCourse);
        }
    }


    public void notifyAllWaitlistedMembers(OfferedCourses offeredCourse) {
        List<WaitList> waitlistedMembers = waitlistRepository.findByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourse.getOfferedCourseId(), IsWaitListed.YES);

        if (!waitlistedMembers.isEmpty()) {
            for (WaitList waitList : waitlistedMembers) {
                FamilyMembers waitlistedFamilyMember = waitList.getFamilyMember();
                waitList.setIsWaitListed(IsWaitListed.NO);
                waitlistRepository.save(waitList);
                String memberName = waitlistedFamilyMember.getMemberName();
                String name = offeredCourse.getCourses().getName();

                String message = "Hello " + memberName + ", a seat has opened up for the course: " + name +
                        ". Please proceed with your enrollment if you wish to join.";
                smsService.sendSms(waitlistedFamilyMember.getHomePhoneNo(), message);
            }
        }
    }

    @Transactional
    public FamilyCourseRegistrationDTO updateFamilyMemberRegistration(Long id, FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        Optional<FamilyCourseRegistrations> existingRegistrationOpt = familyCourseRegistrationRepository.findById(id);

        if (existingRegistrationOpt.isEmpty()) {
            throw new InvalidMemberIdException("Family course registration not found for the given id.");
        }
        FamilyCourseRegistrations existingRegistration = existingRegistrationOpt.get();
        if (familyCourseRegistrationDTO.getEnrollmentDate() != null) {
            existingRegistration.setEnrollmentDate(familyCourseRegistrationDTO.getEnrollmentDate());
        }
        if (familyCourseRegistrationDTO.getWithdrawnCredits() != null) {
            existingRegistration.setWithdrawnCredits(familyCourseRegistrationDTO.getWithdrawnCredits());
        }
        if (familyCourseRegistrationDTO.getLastUpdatedTime() != null) {
            existingRegistration.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        }
        existingRegistration.setLastUpdateBy(familyCourseRegistrationDTO.getLastUpdateBy());
        familyCourseRegistrationRepository.save(existingRegistration);
        return mapToDTO(existingRegistration);
    }

    private FamilyCourseRegistrationDTO mapToDTO(FamilyCourseRegistrations familyCourseRegistrations) {
        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyCourseRegistrationId(familyCourseRegistrations.getFamilyCourseRegistrationId());
        dto.setEnrollmentDate(familyCourseRegistrations.getEnrollmentDate());
        dto.setWithdrawnCredits(familyCourseRegistrations.getWithdrawnCredits());
        dto.setCreatedAt(familyCourseRegistrations.getCreatedAt());
        dto.setOfferedCourseId(familyCourseRegistrations.getOfferedCourseId().getOfferedCourseId());
        dto.setFamilyMemberId(familyCourseRegistrations.getFamilyMemberId().getFamilyMemberId());
        dto.setLastUpdatedTime(familyCourseRegistrations.getLastUpdatedTime());
        dto.setCreatedBy(familyCourseRegistrations.getCreatedBy());
        dto.setLastUpdateBy(familyCourseRegistrations.getLastUpdateBy());
        dto.setIsWithdrawn(familyCourseRegistrations.getIsWithdrawn());
        return dto;
    }

    public List<WaitList> getWaitlistedMembers() {
        return waitlistRepository.findByIsWaitListed(IsWaitListed.YES);
    }
    public List<Courses> getCoursesForWaitlistedMembers() {
        List<WaitList> waitlistedMembers = getWaitlistedMembers();
        List<Courses> waitlistedCourses = new ArrayList<>();
        for (WaitList registration : waitlistedMembers) {
            OfferedCourses offeredCourse = registration.getOfferedCourses();
            if (offeredCourse != null) {
                waitlistedCourses.add(offeredCourse.getCourses());
            }
        }
        return waitlistedCourses;
    }



}
