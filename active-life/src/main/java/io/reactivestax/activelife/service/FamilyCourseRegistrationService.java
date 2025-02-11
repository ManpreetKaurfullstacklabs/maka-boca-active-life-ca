package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.repository.memberregistration.FamilyGroupRepository;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.domain.course.WaitList;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.WaitlistRepository;
import io.reactivestax.activelife.utility.distribution.SmsService;
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
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private SmsService smsService;

    private static final int MAX_WAITLIST_SIZE = 5;

    @Transactional
    public String enrollFamilyMemberInCourse(FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        OfferedCourses offeredCourse = getOfferedCourse(familyCourseRegistrationDTO.getOfferedCourseId());
        Long availableSeats = offeredCourse.getNoOfSeats();
        Long enrolledCount = familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO);
        MemberRegistration familyMember = getFamilyMember(familyCourseRegistrationDTO.getFamilyMemberId());
        Optional<FamilyCourseRegistrations> existingRegistration = familyCourseRegistrationRepository
                .findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse);

        if (existingRegistration.isPresent() && existingRegistration.get().getIsWithdrawn() == IsWithdrawn.NO) {
            throw new RuntimeException("Family member is already enrolled in this course.");
        }

        if (existingRegistration.isPresent() && existingRegistration.get().getIsWithdrawn() == IsWithdrawn.YES) {
            if (enrolledCount < availableSeats) {
                return reEnrollMember(existingRegistration.get());
            } else {
                return addToWaitlist(familyCourseRegistrationDTO.getFamilyMemberId(), familyCourseRegistrationDTO.getOfferedCourseId());
            }
        }

        if (enrolledCount < availableSeats) {
            return enrollMember(familyCourseRegistrationDTO, IsWaitListed.NO, IsWithdrawn.NO);
        } else {
            return handleWaitlist(familyCourseRegistrationDTO.getFamilyMemberId(), familyCourseRegistrationDTO.getOfferedCourseId());
        }
    }


    @Transactional
    public void deleteFamilyMemberFromRegisteredCourse(Long id) {
        Optional<FamilyCourseRegistrations> registrationOpt = familyCourseRegistrationRepository.findById(id);
        if (registrationOpt.isEmpty()) {
            throw new InvalidMemberIdException("Member is not enrolled in any course.");
        }

        FamilyCourseRegistrations familyCourseRegistrations = registrationOpt.get();

        if (familyCourseRegistrations.getIsWithdrawn().equals(IsWithdrawn.YES)) {
            throw new RuntimeException("Member already withdrawn from the course.");
        }
        Long courseFee = familyCourseRegistrations.getCost();
        Long withdrawalCredits = familyCourseRegistrations.getWithdrawnCredits();
        Long balance = courseFee - withdrawalCredits;
        if (balance > 0) {
            familyCourseRegistrations.setWithdrawnCredits(balance);
        } else {
            familyCourseRegistrations.setWithdrawnCredits(0L);
        }
        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.YES);

        Long withdrawnCreditsForGroup = familyCourseRegistrations.getWithdrawnCredits();
        Long groupId = familyCourseRegistrations.getFamilyMemberId().getFamilyGroupId().getFamilyGroupId();
        Optional<FamilyGroups> groupOpt = familyGroupRepository.findById(groupId);

        if (groupOpt.isPresent()) {
            FamilyGroups group = groupOpt.get();
            Long currentWithdrawnCredits = group.getCredits() != null ? group.getCredits() : 0L;
            group.setCredits(currentWithdrawnCredits + withdrawnCreditsForGroup);
            familyGroupRepository.save(group);
        }

        familyCourseRegistrationRepository.save(familyCourseRegistrations);

        OfferedCourses offeredCourse = familyCourseRegistrations.getOfferedCourseId();

        List<WaitList> waitlistedMembers = waitlistRepository.findByOfferedCourses_OfferedCourseIdAndIsWaitListed(
                offeredCourse.getOfferedCourseId(), IsWaitListed.YES
        );

        if (!waitlistedMembers.isEmpty()) {
            WaitList firstWaitlistedMember = waitlistedMembers.get(0);
            MemberRegistration memberToEnroll = firstWaitlistedMember.getFamilyMember();
            firstWaitlistedMember.setIsWaitListed(IsWaitListed.NO);
            waitlistRepository.save(firstWaitlistedMember);

            Optional<FamilyCourseRegistrations> existingEnrollment =
                    familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(memberToEnroll, offeredCourse);

            if (existingEnrollment.isPresent()) {
                FamilyCourseRegistrations updatedEnrollment = existingEnrollment.get();
                updatedEnrollment.setIsWithdrawn(IsWithdrawn.NO);
                updatedEnrollment.setWithdrawnCredits(0L);
                familyCourseRegistrationRepository.save(updatedEnrollment);
            }
        }
    }


    public String handleWaitlist(Long familyMemberId, Long offeredCourseId) {
        Long waitlistCount = waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourseId, IsWaitListed.YES);
        if (waitlistCount < MAX_WAITLIST_SIZE) {
         return   addToWaitlist(familyMemberId, offeredCourseId);
        } else {
            return "Waitlist is full for this course.";
        }
    }


    public String addToWaitlist(Long familyMemberId, Long offeredCourseId) {
        MemberRegistration familyMember = getFamilyMember(familyMemberId);
        OfferedCourses offeredCourse = getOfferedCourse(offeredCourseId);
        Optional<WaitList> existingWaitlistOpt = waitlistRepository.findByFamilyMemberAndOfferedCourses(familyMember, offeredCourse);
        if (existingWaitlistOpt.isPresent()) {
            WaitList existingWaitlist = existingWaitlistOpt.get();
            if (existingWaitlist.getIsWaitListed().equals(IsWaitListed.YES)) {
                return "Family member is already waitlisted for this course.";
            } else {
                existingWaitlist.setIsWaitListed(IsWaitListed.YES);
                waitlistRepository.save(existingWaitlist);
                return "Family member has been moved to waitlist for this course.";
            }
        }

        if (waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourseId, IsWaitListed.YES) < MAX_WAITLIST_SIZE) {
            WaitList newWaitList = new WaitList();
            newWaitList.setFamilyMember(familyMember);
            newWaitList.setOfferedCourses(offeredCourse);
            newWaitList.setNoOfSeats(1L);
            newWaitList.setIsWaitListed(IsWaitListed.YES);
            waitlistRepository.save(newWaitList);
            return "Course seats full. Adding to waitlist.";
        } else {
            return "Waitlist is full for this course.";
        }
    }


    @Transactional
    public String enrollMember(FamilyCourseRegistrationDTO familyCourseRegistrationDTO, IsWaitListed waitListed, IsWithdrawn isWithdrawn) {
        MemberRegistration familyMember = getFamilyMember(familyCourseRegistrationDTO.getFamilyMemberId());
        OfferedCourses offeredCourse = getOfferedCourse(familyCourseRegistrationDTO.getOfferedCourseId());

        Optional<FamilyCourseRegistrations> existingRegistration = familyCourseRegistrationRepository
                .findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse);

        if (existingRegistration.isPresent()) {
            FamilyCourseRegistrations registration = existingRegistration.get();
            registration.setIsWithdrawn(isWithdrawn);
            registration.setWithdrawnCredits(0L);
            registration.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
            familyCourseRegistrationRepository.save(registration);
            return "Member successfully re-enrolled in course " + familyCourseRegistrationDTO.getOfferedCourseId();
        }

        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setFamilyMemberId(familyMember);
        familyCourseRegistrations.setOfferedCourseId(offeredCourse);
        familyCourseRegistrations.setCost(getCostOfferedFromCourses(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setEnrollmentDate(familyCourseRegistrationDTO.getEnrollmentDate());
        familyCourseRegistrations.setIsWithdrawn(isWithdrawn);
        familyCourseRegistrations.setWithdrawnCredits(0L);
        familyCourseRegistrations.setNoOfseats(getNoOfSeatsFromOfferedCoursesTable(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setEnrollmentActorId(familyCourseRegistrationDTO.getFamilyMemberId());
        familyCourseRegistrations.setCreatedAt(familyCourseRegistrationDTO.getCreatedAt());
        familyCourseRegistrations.setIsWaitListed(waitListed);
        familyCourseRegistrations.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        familyCourseRegistrations.setCreatedBy(familyCourseRegistrationDTO.getCreatedBy());
        familyCourseRegistrations.setLastUpdateBy(familyCourseRegistrationDTO.getLastUpdateBy());

        familyCourseRegistrationRepository.save(familyCourseRegistrations);
        return "Member successfully enrolled in course " + familyCourseRegistrationDTO.getOfferedCourseId();
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

    public MemberRegistration getFamilyMember(Long familyMemberId) {
        Optional<MemberRegistration> byId = memberRegistrationRepository.findById(familyMemberId);
        if (byId.isEmpty()) {
            throw new InvalidMemberIdException("Family member does not exist.");
        }
        MemberRegistration memberRegistration = byId.get();
        if (memberRegistration.getStatus().equals(Status.INACTIVE)) {
            throw new RuntimeException("Enrollment is not available for this member because it's inactive.");
        }
        return memberRegistration;
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

    public FamilyCourseRegistrationDTO getAllFamilyMemberRegistration(Long id) {
        Optional<FamilyCourseRegistrations> byId = familyCourseRegistrationRepository.findById(id);
        if (byId.isEmpty()) {
            throw new InvalidMemberIdException("Family course registration not found.");
        }
        FamilyCourseRegistrations familyCourseRegistrations = byId.get();
        FamilyCourseRegistrationDTO familyCourseRegistrationDTO = new FamilyCourseRegistrationDTO();
        MemberRegistration member = memberIsEnrolledOrNot(familyCourseRegistrations.getFamilyMemberId().getFamilyMemberId());
        familyCourseRegistrationDTO.setFamilyMemberId(member.getFamilyMemberId());
        familyCourseRegistrationDTO.setEnrollmentDate(familyCourseRegistrations.getEnrollmentDate());
        familyCourseRegistrationDTO.setCreatedAt(familyCourseRegistrations.getCreatedAt());
        OfferedCourses offeredCourses = offeredCourseExistsOrNot(familyCourseRegistrations.getOfferedCourseId().getOfferedCourseId());
        familyCourseRegistrationDTO.setOfferedCourseId(offeredCourses.getOfferedCourseId());
        familyCourseRegistrationDTO.setCreatedBy(familyCourseRegistrations.getCreatedBy());
        familyCourseRegistrationDTO.setLastUpdatedTime(familyCourseRegistrations.getLastUpdatedTime());
        familyCourseRegistrationDTO.setLastUpdateBy(familyCourseRegistrations.getLastUpdateBy());
        familyCourseRegistrationDTO.setIsWithdrawn(familyCourseRegistrations.getIsWithdrawn());

        return familyCourseRegistrationDTO;
    }

    public MemberRegistration memberIsEnrolledOrNot(Long id) {
        Optional<MemberRegistration> byId = memberRegistrationRepository.findById(id);
        MemberRegistration memberRegistration = byId.orElseThrow(() -> new InvalidMemberIdException("Member not found."));
        if (memberRegistration.getStatus().equals(Status.INACTIVE)) {
            throw new InvalidMemberIdException("Member is inactive.");
        }
        return memberRegistration;
    }


    public void notifyAllWaitlistedMembers(OfferedCourses offeredCourse) {
        List<WaitList> waitlistedMembers = waitlistRepository.findByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourse.getOfferedCourseId(), IsWaitListed.YES);

        if (!waitlistedMembers.isEmpty()) {
            for (WaitList waitList : waitlistedMembers) {
                MemberRegistration waitlistedFamilyMember = waitList.getFamilyMember();
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

        if (familyCourseRegistrationDTO.getLastUpdatedTime() != null) {
            existingRegistration.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        }
        existingRegistration.setLastUpdateBy(familyCourseRegistrationDTO.getLastUpdateBy());
        familyCourseRegistrationRepository.save(existingRegistration);
        return mapToDTO(existingRegistration);
    }

    public FamilyCourseRegistrationDTO mapToDTO(FamilyCourseRegistrations familyCourseRegistrations) {
        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyCourseRegistrationId(familyCourseRegistrations.getFamilyCourseRegistrationId());
        dto.setEnrollmentDate(familyCourseRegistrations.getEnrollmentDate());
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

    @Transactional
   public String reEnrollMember(FamilyCourseRegistrations existingRegistration) {
            existingRegistration.setIsWithdrawn(IsWithdrawn.NO);
            existingRegistration.setWithdrawnCredits(existingRegistration.getWithdrawnCredits());
            familyCourseRegistrationRepository.save(existingRegistration);
            return "Previously withdrawn member successfully reenrolled ";
    }


}
