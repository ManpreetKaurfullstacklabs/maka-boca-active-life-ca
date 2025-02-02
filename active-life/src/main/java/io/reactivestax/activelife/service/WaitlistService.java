//package io.reactivestax.activelife.service;
//
//import io.reactivestax.activelife.Enums.IsWaitListed;
//import io.reactivestax.activelife.Enums.Status;
//import io.reactivestax.activelife.domain.WaitList;
//import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
//import io.reactivestax.activelife.domain.membership.FamilyMembers;
//import io.reactivestax.activelife.domain.course.OfferedCourses;
//import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
//
//import io.reactivestax.activelife.repository.familymember.FamilMemberRepository;
//import io.reactivestax.activelife.repository.familymember.FamilyCourseRegistrationRepository;
//import io.reactivestax.activelife.repository.familymember.WaitlistRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class WaitlistService {
//
//    @Autowired
//    private OfferedCourseRepository offeredCourseRepository;
//
//    @Autowired
//    private FamilyCourseRegistrationService familyCourseRegistrationService;
//
//    @Autowired
// private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;
//
//    @Autowired
//    private FamilMemberRepository familyMembersRepository;
//
//    @Autowired
//    private WaitlistRepository waitListRepository;
//
//
//    public void enrollOrWaitlist(Long familyMemberId, Long offeredCourseId) {
//        OfferedCourses offeredCourse = getOfferedCourse(offeredCourseId);
//        FamilyMembers familyMember = getFamilyMember(familyMemberId);
//
//        // Check if the course has available seats
//        if (offeredCourse.getNoOfSeats() > 0) {
//            enrollFamilyMember(familyMember, offeredCourse);
//        } else {
//            // Add to waitlist if no seats available
//            addToWaitlist(familyMember, offeredCourse);
//        }
//    }
//
//    /**
//     * Enroll the family member in the course.
//     */
//    private void enrollFamilyMember(FamilyMembers familyMember, OfferedCourses offeredCourse) {
//        FamilyCourseRegistrations registration = new FamilyCourseRegistrations();
//        registration.setFamilyMemberId(familyMember);
//        registration.setOfferedCourseId(offeredCourse);
//        registration.setWaitListed(IsWaitListed.NO); // Set to NO since the member is enrolled
//        familyCourseRegistrationRepository.save(registration);
//        offeredCourse.setNoOfSeats(offeredCourse.getNoOfSeats() - 1); // Decrease available seats
//        offeredCourseRepository.save(offeredCourse);
//    }
//
//    /**
//     * Add a family member to the waitlist.
//     */
//    private void addToWaitlist(FamilyMembers familyMember, OfferedCourses offeredCourse) {
//        WaitList waitList = new WaitList();
//        waitList.setFamilyMember(familyMember);
//        waitList.setOfferedCourses(offeredCourse);
//        waitList.setStatus(Status.INACTIVE); // Mark the member as pending on the waitlist
//        waitList.setNoOfSeats(1L); // Assuming one seat per member
//        waitListRepository.save(waitList);
//    }
//
//    /**
//     * Move a member from the waitlist to an enrolled state if a seat becomes available.
//     */
//    public void moveWaitlistedToEnrollment(Long offeredCourseId) {
//        // Fetch the course details
//        OfferedCourses offeredCourse = getOfferedCourse(offeredCourseId);
//
//        // Check if there is a waitlist and if seats are available
//        if (offeredCourse.getNoOfSeats() > 0) {
//            // Retrieve the first person in the waitlist
//            Optional<WaitList> firstWaitlisted = waitListRepository.findFirstByOfferedCoursesIdAndStatus(offeredCourseId, IsWaitListed.NO);
//
//            if (firstWaitlisted.isPresent()) {
//                WaitList waitlistEntry = firstWaitlisted.get();
//                FamilyMembers familyMember = waitlistEntry.getFamilyMember();
//
//                // Enroll the family member
//                enrollFamilyMember(familyMember, offeredCourse);
//
//                // Remove them from the waitlist
//                waitListRepository.delete(waitlistEntry);
//            }
//        }
//    }
//
//    /**
//     * Fetch the OfferedCourse from the database.
//     */
//    private OfferedCourses getOfferedCourse(Long offeredCourseId) {
//        return offeredCourseRepository.findById(offeredCourseId)
//                .orElseThrow(() -> new IllegalArgumentException("Offered course not found"));
//    }
//
//    /**
//     * Fetch the FamilyMember from the database.
//     */
//    private FamilyMembers getFamilyMember(Long familyMemberId) {
//        return familyMembersRepository.findById(familyMemberId)
//                .orElseThrow(() -> new IllegalArgumentException("Family member not found"));
//    }
//}
