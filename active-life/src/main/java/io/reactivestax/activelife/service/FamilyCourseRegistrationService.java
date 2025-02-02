package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.domain.WaitList;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.familymember.FamilMemberRepository;
import io.reactivestax.activelife.repository.familymember.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.familymember.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FamilyCourseRegistrationService {

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private FamilMemberRepository familMemberRepository;

    @Autowired
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;




    @Transactional
    public void enrollFamilyMemberInCourse(Long familyMemberId, Long offeredCourseId) {

        // Retrieve the offered course
        OfferedCourses offeredCourse = getOfferedCourse(offeredCourseId);
        Long availableSeats = offeredCourse.getNoOfSeats();

        // Count the number of members already enrolled (non-withdrawn)
        Long enrolledCount = familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO);

        // If there are seats available, proceed with enrollment
        if (enrolledCount < availableSeats) {
            // Proceed with regular enrollment
            enrollMember(familyMemberId, offeredCourseId, offeredCourse.getCost(), IsWaitListed.NO);
        } else {
            // If no seats are available, check the waitlist
            handleWaitlist(familyMemberId, offeredCourseId, availableSeats);
        }
    }


    private void handleWaitlist(Long familyMemberId, Long offeredCourseId, Long availableSeats) {

        // Check the number of people already on the waitlist for the course
        Long waitlistCount = familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWaitListed(getOfferedCourse(offeredCourseId), IsWaitListed.YES);

        // If there's space on the waitlist
        if (waitlistCount < availableSeats) {
            // Add the member to the waitlist
            addToWaitlist(familyMemberId, offeredCourseId);
        } else {
            // If the waitlist is full, throw an exception
            throw new RuntimeException("Waitlist is full for this course.");
        }
    }


    private void addToWaitlist(Long familyMemberId, Long offeredCourseId) {
        FamilyMembers familyMember = getFamilyMember(familyMemberId);
        OfferedCourses offeredCourse = getOfferedCourse(offeredCourseId);

        WaitList waitList = new WaitList();
        waitList.setFamilyMember(familyMember);
        waitList.setOfferedCourses(offeredCourse);
        waitList.setStatus(Status.INACTIVE); // Mark the member as pending on the waitlist
        waitList.setNoOfSeats(1L); // Assuming one seat per member
        waitlistRepository.save(waitList);
    }

    private void enrollMember(Long familyMemberId, Long offeredCourseId, Long fee, IsWaitListed waitListed) {
        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setFamilyMemberId(getFamilyMember(familyMemberId));
        familyCourseRegistrations.setOfferedCourseId(getOfferedCourse(offeredCourseId));
        familyCourseRegistrations.setCost(fee);
        familyCourseRegistrations.setEnrollmentDate(java.time.LocalDate.now());
        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.NO);
        familyCourseRegistrations.setIsWaitListed(waitListed);
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

    private FamilyMembers getFamilyMember(Long familyMemberId) {
        Optional<FamilyMembers> byId = familMemberRepository.findById(familyMemberId);
        if (byId.isEmpty()) {
            throw new InvalidMemberIdException("Family member does not exist.");
        }
        FamilyMembers familyMembers = byId.get();
        if (familyMembers.getStatus().equals(Status.INACTIVE)) {
            throw new RuntimeException("Enrollment is not available for this member because it's inactive.");
        }
        return familyMembers;
    }

    @Transactional
    public void enrollFamilyMemberToFamilyRegistration(FamilyCourseRegistrationDTO familyCourseRegistrationDTO) {
        // Validate family member
        Optional<FamilyMembers> familyMemberOpt = familMemberRepository.findById(familyCourseRegistrationDTO.getFamilyMemberId());
        if (familyMemberOpt.isEmpty()) {
            throw new InvalidMemberIdException("Family member does not exist.");
        }
        FamilyMembers familyMember = familyMemberOpt.get();
        if (familyMember.getStatus().equals(Status.INACTIVE)) {
            throw new InvalidMemberIdException("Cannot enroll inactive member.");
        }

        // Validate course
        OfferedCourses offeredCourses = offeredCourseExistsOrNot(familyCourseRegistrationDTO.getOfferedCourseId());

        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setCost(getCostOfferedFromCourses(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setEnrollmentDate(familyCourseRegistrationDTO.getEnrollmentDate());
        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.NO);
        familyCourseRegistrations.setWithdrawnCredits(0L);
        familyCourseRegistrations.setNoOfseats(getNoOfSeatsFromOfferedCoursesTable(familyCourseRegistrationDTO.getOfferedCourseId()));
        familyCourseRegistrations.setEnrollmentActorId(familyCourseRegistrationDTO.getFamilyMemberId());
        familyCourseRegistrations.setCreatedAt(familyCourseRegistrationDTO.getCreatedAt());
        familyCourseRegistrations.setOfferedCourseId(offeredCourses);
        familyCourseRegistrations.setFamilyMemberId(memberIsActiveOrNot(familyCourseRegistrationDTO.getFamilyMemberId()));
        familyCourseRegistrations.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        familyCourseRegistrations.setCreatedBy(familyCourseRegistrationDTO.getCreatedBy());
        familyCourseRegistrations.setLastUpdateBy(familyCourseRegistrationDTO.getLastUpdateBy());

        familyCourseRegistrationRepository.save(familyCourseRegistrations);
    }

    public Long getNoOfSeatsFromOfferedCoursesTable(Long id) {
        Optional<OfferedCourses> byId = offeredCourseRepository.findById(id);
        if (byId.isEmpty()) {
            throw new InvalidCourseIdException("Course does not exist.");
        }
        OfferedCourses offeredCourses = byId.get();
        return offeredCourses.getNoOfSeats();
    }

    public Long getCostOfferedFromCourses(Long id) {
        Optional<OfferedCourses> byId = offeredCourseRepository.findById(id);
        if (byId.isEmpty()) {
            throw new InvalidCourseIdException("Course does not exist.");
        }
        OfferedCourses offeredCourses = byId.get();
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
        Optional<FamilyMembers> byId = familMemberRepository.findById(id);
        if (byId.isEmpty()) {
            throw new InvalidMemberIdException("Member does not exist.");
        }
        FamilyMembers familyMembers = byId.get();
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
        Optional<FamilyMembers> byId = familMemberRepository.findById(id);
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
    }
}
