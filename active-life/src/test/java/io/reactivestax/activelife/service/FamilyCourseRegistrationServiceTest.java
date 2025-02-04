package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.*;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import io.reactivestax.activelife.utility.distribution.SmsService;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.course.WaitList;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.exception.InvalidCourseIdException;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseFeeRepository;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.WaitlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

class FamilyCourseRegistrationServiceTest {

    @Mock
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Mock
    private OfferedCourseRepository offeredCourseRepository;

    @Mock
    private MemberRegistrationRepository memberRegistrationRepository;

    @Mock
    private OfferedCourseFeeRepository offeredCourseFeeRepository;

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.now();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testEnrollFamilyMemberInCourse_whenSeatsAvailable() {

        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();
        offeredCourseFee.setFeeId(1L);
        offeredCourseFee.setCourseFee(100L);
        offeredCourseFee.setFeeType(FeeType.RESIDENT);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(1L);
        offeredCourse.setNoOfSeats(10L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);
        offeredCourse.setOfferedCourseFee(offeredCourseFee);

        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setOfferedCourseId(1L);
        dto.setFamilyMemberId(1L);
        dto.setEnrollmentDate(localDate);
        dto.setCreatedBy(1L);
        dto.setLastUpdatedTime(localDateTime);
        dto.setWithdrawnCredits(0L);
        dto.setCreatedAt(localDateTime);
        dto.setLastUpdateBy(1L);
        dto.setOfferedCourseId(offeredCourse.getOfferedCourseId());
        dto.setIsWithdrawn(IsWithdrawn.NO);









        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        familyMember.setStatus(Status.ACTIVE);

        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(offeredCourse));
        when(memberRegistrationRepository.findById(1L)).thenReturn(Optional.of(familyMember));
        when(familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(any(), any())).thenReturn(1L);

        familyCourseRegistrationService.enrollFamilyMemberInCourse(dto);

        verify(familyCourseRegistrationRepository, times(1)).save(any(FamilyCourseRegistrations.class));
    }


    @Test
    void testEnrollFamilyMemberInCourse_FamilyMemberAlreadyEnrolled() {
        Long familyMemberId = 1L;
        Long offeredCourseId = 100L;
        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyMemberId(familyMemberId);
        dto.setOfferedCourseId(offeredCourseId);

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(familyMemberId);
        familyMember.setStatus(Status.ACTIVE);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(offeredCourseId);
        offeredCourse.setNoOfSeats(10L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        FamilyCourseRegistrations existingRegistration = new FamilyCourseRegistrations();
        existingRegistration.setFamilyMemberId(familyMember);
        existingRegistration.setOfferedCourseId(offeredCourse);

        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse)).thenReturn(Optional.of(existingRegistration));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            familyCourseRegistrationService.enrollFamilyMemberInCourse(dto);
        });

        assertEquals("Family member is already enrolled in this course.", exception.getMessage());
    }

    @Test
    void testEnrollFamilyMemberInCourse_WaitlistFull() {

        Long familyMemberId = 1L;
        Long offeredCourseId = 100L;

        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyMemberId(familyMemberId);
        dto.setOfferedCourseId(offeredCourseId);


        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(familyMemberId);
        familyMember.setStatus(Status.ACTIVE);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(offeredCourseId);
        offeredCourse.setNoOfSeats(5L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.of(offeredCourse));
        when(familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO)).thenReturn(5L);
        when(waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(offeredCourseId, IsWaitListed.YES)).thenReturn(5L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            familyCourseRegistrationService.enrollFamilyMemberInCourse(dto);
        });

        assertEquals("Waitlist is full for this course.", exception.getMessage());
    }


    @Test
    void testDeleteFamilyMemberFromRegisteredCourse_whenWithdrawn() {

        FamilyCourseRegistrations registration = new FamilyCourseRegistrations();
        registration.setIsWithdrawn(IsWithdrawn.YES);
        when(familyCourseRegistrationRepository.findById(anyLong())).thenReturn(Optional.of(registration));

        assertThrows(RuntimeException.class, () -> {
            familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(1L);
        });
    }

    @Test
    void testDeleteFamilyMemberFromRegisteredCourse_whenSuccessful() {
        OfferedCourses offeredCourses = new OfferedCourses();
        offeredCourses.setOfferedCourseId(1L);
        FamilyCourseRegistrations registration = new FamilyCourseRegistrations();
        registration.setIsWithdrawn(IsWithdrawn.NO);
        registration.setOfferedCourseId(offeredCourses);
        when(familyCourseRegistrationRepository.findById(anyLong())).thenReturn(Optional.of(registration));
        when(waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(anyLong(), any())).thenReturn(1L);


        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(1L);

        verify(familyCourseRegistrationRepository, times(1)).save(registration);
    }


    @Test
    void testEnrollFamilyMemberInCourse_whenAlreadyEnrolled() {

        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setOfferedCourseId(1L);
        dto.setFamilyMemberId(101L);
        dto.setEnrollmentDate(localDate);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(1L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(101L);
        familyMember.setStatus(Status.ACTIVE);

        FamilyCourseRegistrations existingRegistration = new FamilyCourseRegistrations();
        existingRegistration.setFamilyMemberId(familyMember);
        existingRegistration.setOfferedCourseId(offeredCourse);

        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(offeredCourse));
        when(memberRegistrationRepository.findById(101L)).thenReturn(Optional.of(familyMember));
        when(familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse)).thenReturn(Optional.of(existingRegistration));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            familyCourseRegistrationService.enrollFamilyMemberInCourse(dto);
        });
        assertEquals("Family member is already enrolled in this course.", exception.getMessage());
    }

    @Test
    void testAddToWaitlist() {

        Long familyMemberId = 101L;
        Long offeredCourseId = 1L;

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(familyMemberId);
        familyMember.setStatus(Status.ACTIVE);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(offeredCourseId);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(familyMember));
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.of(offeredCourse));

        WaitList waitList = new WaitList();
        waitList.setFamilyMember(familyMember);
        waitList.setOfferedCourses(offeredCourse);
        waitList.setNoOfSeats(1L);
        waitList.setIsWaitListed(IsWaitListed.YES);

        familyCourseRegistrationService.addToWaitlist(familyMemberId, offeredCourseId);

        verify(waitlistRepository, times(1)).save(waitList);

        ArgumentCaptor<WaitList> captor = ArgumentCaptor.forClass(WaitList.class);
        verify(waitlistRepository).save(captor.capture());
        WaitList capturedWaitlist = captor.getValue();

        assertEquals(familyMember, capturedWaitlist.getFamilyMember());
        assertEquals(offeredCourse, capturedWaitlist.getOfferedCourses());
        assertEquals(Long.valueOf(1), capturedWaitlist.getNoOfSeats());
        assertEquals(IsWaitListed.YES, capturedWaitlist.getIsWaitListed());
    }

    @Test
    void testUpdateFamilyMemberRegistration() {

        Long familyMemberId = 101L;
        Long offeredCourseId = 1L;

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(familyMemberId);
        familyMember.setStatus(Status.INACTIVE);
        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(offeredCourseId);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        FamilyCourseRegistrations existingRegistration = new FamilyCourseRegistrations();
        existingRegistration.setFamilyMemberId(familyMember);
        existingRegistration.setOfferedCourseId(offeredCourse);
        when(familyCourseRegistrationRepository.findById(anyLong())).thenReturn(Optional.of(existingRegistration));

        familyCourseRegistrationService.updateFamilyMemberRegistration(1L, new FamilyCourseRegistrationDTO());

        verify(familyCourseRegistrationRepository, times(1)).save(existingRegistration);
    }

    @Test
    void testNotifyAllWaitlistedMembers() {
        WaitList waitList = new WaitList();
        MemberRegistration waitlistedFamilyMember = new MemberRegistration();
        waitList.setFamilyMember(waitlistedFamilyMember);
        waitlistRepository.save(waitList);
        familyCourseRegistrationService.notifyAllWaitlistedMembers(new OfferedCourses());

    }


    @Test
    void testGetCostOfferedFromCourses_whenFeeDoesNotExist() {
        Long offeredCourseId = 1L;
        when(offeredCourseFeeRepository.findById(offeredCourseId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            familyCourseRegistrationService.getCostOfferedFromCourses(offeredCourseId);
        });
    }

    @Test
    void testGetNoOfSeatsFromOfferedCoursesTable_whenCourseExists() {
        OfferedCourses offeredCourse = new OfferedCourses();
        Long offeredCourseId = 1L;
        offeredCourse.setNoOfSeats(10L);
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.of(offeredCourse));
        Long result = familyCourseRegistrationService.getNoOfSeatsFromOfferedCoursesTable(offeredCourseId);
        assertEquals(Long.valueOf(10), result);
    }

    @Test
    void testGetNoOfSeatsFromOfferedCoursesTable_whenCourseDoesNotExist() {
        Long offeredCourseId = 1L;
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.empty());
        assertThrows(InvalidCourseIdException.class, () -> {
            familyCourseRegistrationService.getNoOfSeatsFromOfferedCoursesTable(offeredCourseId);
        });
    }

    @Test
     void testGetFamilyMember_whenMemberExists() {
        Long familyMemberId = 1L;
        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        familyMember.setStatus(Status.ACTIVE);
        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(familyMember));

        MemberRegistration result = familyCourseRegistrationService.getFamilyMember(familyMemberId);

        assertNotNull(result);
        assertEquals(familyMemberId, result.getFamilyMemberId());
    }

    @Test
     void testGetFamilyMember_whenMemberDoesNotExist() {
        Long familyMemberId = 1L;
        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.empty());

        assertThrows(InvalidMemberIdException.class, () -> {
            familyCourseRegistrationService.getFamilyMember(familyMemberId);
        });
    }





}
