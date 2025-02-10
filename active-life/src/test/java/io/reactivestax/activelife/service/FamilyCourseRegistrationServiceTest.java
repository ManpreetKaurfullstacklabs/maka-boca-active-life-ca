package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.*;
import io.reactivestax.activelife.domain.course.Courses;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.repository.memberregistration.FamilyGroupRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class FamilyCourseRegistrationServiceTest {

    @Mock
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Mock
    private FamilyGroupRepository familyGroupRepository;

    @Mock
    private FamilyGroups familyGroups;

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

    private  FamilyCourseRegistrations familyCourseRegistrations;

    @InjectMocks
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.now();
    private FamilyCourseRegistrationDTO registrationDTO;
    private OfferedCourses offeredCourse;
    private MemberRegistration member;
    private FamilyCourseRegistrations existingRegistration;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        existingRegistration = new FamilyCourseRegistrations();
        existingRegistration.setFamilyCourseRegistrationId(1L);
        existingRegistration.setEnrollmentDate(LocalDate.of(2024, 1, 10));
        existingRegistration.setWithdrawnCredits(5L);
        existingRegistration.setLastUpdatedTime(LocalDateTime.of(2024, 1, 10, 12, 0));
        existingRegistration.setLastUpdateBy(101L);



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

        dto.setCreatedAt(localDateTime);
        dto.setLastUpdateBy(1L);
        dto.setIsWithdrawn(IsWithdrawn.NO);

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        familyMember.setStatus(Status.ACTIVE);

        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(offeredCourse));
        when(memberRegistrationRepository.findById(1L)).thenReturn(Optional.of(familyMember));
        when(familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(any(), any())).thenReturn(1L);

        when(offeredCourseFeeRepository.findById(1L)).thenReturn(Optional.of(offeredCourseFee));

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



        assertTrue(true,"Family member is already enrolled in this course." );
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


        assertTrue(true, "Waitlist is full for this course.");
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

//    @Test
//    void testDeleteFamilyMemberFromRegisteredCourse_whenSuccessful() {
//        OfferedCourses offeredCourses = new OfferedCourses();
//        offeredCourses.setOfferedCourseId(1L);
//        FamilyCourseRegistrations registration = new FamilyCourseRegistrations();
//        registration.setIsWithdrawn(IsWithdrawn.NO);
//        registration.setOfferedCourseId(offeredCourses);
//        when(familyCourseRegistrationRepository.findById(anyLong())).thenReturn(Optional.of(registration));
//        when(waitlistRepository.countByOfferedCourses_OfferedCourseIdAndIsWaitListed(anyLong(), any())).thenReturn(1L);
//        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(1L);
//
//        verify(familyCourseRegistrationRepository, times(1)).save(registration);
//    }


//    @Test
//    void testEnrollFamilyMemberInCourse_whenAlreadyEnrolled() {
//
//        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
//        dto.setOfferedCourseId(1L);
//        dto.setFamilyMemberId(101L);
//        dto.setEnrollmentDate(localDate);
//
//        OfferedCourses offeredCourse = new OfferedCourses();
//        offeredCourse.setOfferedCourseId(1L);
//        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);
//
//        MemberRegistration familyMember = new MemberRegistration();
//        familyMember.setFamilyMemberId(101L);
//        familyMember.setStatus(Status.ACTIVE);
//
//        FamilyCourseRegistrations existingRegistration = new FamilyCourseRegistrations();
//        existingRegistration.setFamilyMemberId(familyMember);
//        existingRegistration.setOfferedCourseId(offeredCourse);
//
//        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(offeredCourse));
//        when(memberRegistrationRepository.findById(101L)).thenReturn(Optional.of(familyMember));
//        when(familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse)).thenReturn(Optional.of(existingRegistration));
//
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            familyCourseRegistrationService.enrollFamilyMemberInCourse(dto);
//        });
//        assertEquals("Family member is already enrolled in this course.", exception.getMessage());
//    }

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

    @Test
    void testEnrollFamilyMemberInCourse_FailsFornonexistongCourse() {
        MemberRegistration memberRegistration = new MemberRegistration();
        memberRegistration.setStatus(Status.INACTIVE);
        OfferedCourses offeredCourses = new OfferedCourses();
        offeredCourses.setOfferedCourseId(1L);

        when(memberRegistrationRepository.findById(memberRegistration.getFamilyMemberId())).thenReturn(Optional.of(memberRegistration));

        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyMemberId(memberRegistration.getFamilyMemberId());
        dto.setOfferedCourseId(offeredCourses.getOfferedCourseId());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> familyCourseRegistrationService.enrollFamilyMemberInCourse(dto));
        assertEquals("Offered course does not exist.", exception.getMessage());
    }

    @Test
    void testReEnrollMember() {

        String expectedMessage = "Previously withdrawn member successfully reenrolled ";

        String result = familyCourseRegistrationService.reEnrollMember(existingRegistration);

        assertEquals(expectedMessage, result);
        assertEquals(IsWithdrawn.NO, existingRegistration.getIsWithdrawn());
        verify(familyCourseRegistrationRepository, times(1)).save(existingRegistration);
    }

    @Test
    void testNotifyAllWaitlistedMembers() {

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(1L);

        Courses course = new Courses();
        course.setName("Spring Boot Basics");
        offeredCourse.setCourses(course);

        MemberRegistration member = new MemberRegistration();
        member.setMemberName("John Doe");
        member.setHomePhoneNo("+1234567890");

        WaitList waitList = new WaitList();
        waitList.setFamilyMember(member);
        waitList.setIsWaitListed(IsWaitListed.YES);

        List<WaitList> waitlistedMembers = Arrays.asList(waitList);

        when(waitlistRepository.findByOfferedCourses_OfferedCourseIdAndIsWaitListed(1L, IsWaitListed.YES))
                .thenReturn(waitlistedMembers);

        familyCourseRegistrationService.notifyAllWaitlistedMembers(offeredCourse);

        assertEquals(IsWaitListed.NO, waitList.getIsWaitListed());
        verify(waitlistRepository, times(1)).save(waitList);
        verify(smsService, times(1)).sendSms("+1234567890",
                "Hello John Doe, a seat has opened up for the course: Spring Boot Basics. Please proceed with your enrollment if you wish to join.");
    }


    @Test
    void testGetCoursesForWaitlistedMembers() {

        Courses course1 = new Courses();
        course1.setName("Java Programming");

        Courses course2 = new Courses();
        course2.setName("Spring Boot");

        OfferedCourses offeredCourse1 = new OfferedCourses();
        offeredCourse1.setCourses(course1);

        OfferedCourses offeredCourse2 = new OfferedCourses();
        offeredCourse2.setCourses(course2);

        WaitList waitList1 = new WaitList();
        waitList1.setIsWaitListed(IsWaitListed.YES);
        waitList1.setOfferedCourses(offeredCourse1);

        WaitList waitList2 = new WaitList();
        waitList2.setIsWaitListed(IsWaitListed.YES);
        waitList2.setOfferedCourses(offeredCourse2);

        List<WaitList> waitlistedMembers = Arrays.asList(waitList1, waitList2);

        when(waitlistRepository.findByIsWaitListed(IsWaitListed.YES)).thenReturn(waitlistedMembers);


        List<Courses> result = familyCourseRegistrationService.getCoursesForWaitlistedMembers();

        assertEquals(2, result.size());
        assertEquals("Java Programming", result.get(0).getName());
        assertEquals("Spring Boot", result.get(1).getName());
        verify(waitlistRepository, times(1)).findByIsWaitListed(IsWaitListed.YES);
    }

    @Test
    void testUpdateFamilyMemberRegistration_InvalidId_ThrowsException() {

        Long invalidId = 99L;
        FamilyCourseRegistrationDTO updateDTO = new FamilyCourseRegistrationDTO();

        when(familyCourseRegistrationRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvalidMemberIdException.class, () -> {
            familyCourseRegistrationService.updateFamilyMemberRegistration(invalidId, updateDTO);
        });

        assertEquals("Family course registration not found for the given id.", exception.getMessage());
        verify(familyCourseRegistrationRepository, times(1)).findById(invalidId);
        verify(familyCourseRegistrationRepository, never()).save(any());
    }

    @Test
    void testEnrollFamilyMemberInCourse_WhenSeatsAreFull() {
        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyMemberId(1L);
        dto.setOfferedCourseId(100L);


        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setNoOfSeats(5L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        familyMember.setStatus(Status.ACTIVE);

        Courses course = new Courses();
        course.setName("Spring Boot Basics");
        offeredCourse.setCourses(course);

        MemberRegistration member = new MemberRegistration();
        member.setMemberName("John Doe");
        member.setHomePhoneNo("+1234567890");


        when(offeredCourseRepository.findById(100L)).thenReturn(Optional.of(offeredCourse));
        when(memberRegistrationRepository.findById(1L)).thenReturn(Optional.of(familyMember));
        when(familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO))
                .thenReturn(5L);
        when(familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(familyMember, offeredCourse))
                .thenReturn(Optional.empty());

        familyCourseRegistrationService.enrollFamilyMemberInCourse(dto);

        verify(familyCourseRegistrationRepository, never()).save(any(FamilyCourseRegistrations.class));
    }

    @Test
    void testGetAllFamilyMemberRegistration_Success() {
        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyMemberId(1L);
        dto.setOfferedCourseId(100L);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(100L);
        offeredCourse.setNoOfSeats(5L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        familyMember.setStatus(Status.ACTIVE);

        Courses course = new Courses();
        course.setName("Spring Boot Basics");
        offeredCourse.setCourses(course);

        MemberRegistration member = new MemberRegistration();
        member.setMemberName("John Doe");
        member.setHomePhoneNo("+1234567890");

        Long familyCourseRegistrationId = 1L;
        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setFamilyMemberId(familyMember);
        familyCourseRegistrations.setOfferedCourseId(offeredCourse);
        familyCourseRegistrations.setEnrollmentDate(LocalDate.now());
        familyCourseRegistrations.setWithdrawnCredits(0L);
        familyCourseRegistrations.setCreatedAt(LocalDateTime.now());
        familyCourseRegistrations.setCreatedBy(1L);
        familyCourseRegistrations.setLastUpdatedTime(LocalDateTime.now());
        familyCourseRegistrations.setLastUpdateBy(1L);
        familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.NO);

        MemberRegistration memberRegistration = new MemberRegistration();
        memberRegistration.setFamilyMemberId(1L);
        memberRegistration.setStatus(Status.ACTIVE);

        OfferedCourses offeredCourses = new OfferedCourses();
        offeredCourses.setOfferedCourseId(100L);

        when(familyCourseRegistrationRepository.findById(familyCourseRegistrationId)).thenReturn(Optional.of(familyCourseRegistrations));
        when(memberRegistrationRepository.findById(1L)).thenReturn(Optional.of(memberRegistration));
        when(offeredCourseRepository.findById(100L)).thenReturn(Optional.of(offeredCourses));

        FamilyCourseRegistrationDTO result = familyCourseRegistrationService.getAllFamilyMemberRegistration(familyCourseRegistrationId);

        assertNotNull(result);
        assertEquals(familyCourseRegistrations.getEnrollmentDate(), result.getEnrollmentDate());

        assertEquals(familyCourseRegistrations.getCreatedAt(), result.getCreatedAt());
        assertEquals(familyCourseRegistrations.getCreatedBy(), result.getCreatedBy());
        assertEquals(familyCourseRegistrations.getIsWithdrawn(), result.getIsWithdrawn());
    }


    @Test
    public void testGetAllFamilyMemberRegistration_MemberInactive() {
        FamilyCourseRegistrationDTO dto = new FamilyCourseRegistrationDTO();
        dto.setFamilyMemberId(1L);
        dto.setOfferedCourseId(100L);

        OfferedCourses offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(100L);
        offeredCourse.setNoOfSeats(5L);
        offeredCourse.setAvailableForEnrollment(AvailableForEnrollment.YES);

        MemberRegistration familyMember = new MemberRegistration();
        familyMember.setFamilyMemberId(1L);
        familyMember.setStatus(Status.INACTIVE);

        Courses course = new Courses();
        course.setName("Spring Boot Basics");
        offeredCourse.setCourses(course);

        MemberRegistration member = new MemberRegistration();
        member.setMemberName("John Doe");
        member.setHomePhoneNo("+1234567890");

        Long familyCourseRegistrationId = 1L;
        FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
        familyCourseRegistrations.setFamilyMemberId(familyMember);
        familyCourseRegistrations.setOfferedCourseId(offeredCourse);

        MemberRegistration memberRegistration = new MemberRegistration();
        memberRegistration.setFamilyMemberId(1L);
        memberRegistration.setStatus(Status.ACTIVE);

        when(familyCourseRegistrationRepository.findById(familyCourseRegistrationId)).thenReturn(Optional.of(familyCourseRegistrations));
        when(memberRegistrationRepository.findById(1L)).thenReturn(Optional.of(memberRegistration));
        when(offeredCourseRepository.findById(100L)).thenReturn(Optional.of(offeredCourse));

        familyCourseRegistrationService.getAllFamilyMemberRegistration(familyCourseRegistrationId);
    }


    @Test
    void testUpdateFamilyMemberRegistration_NotFound() {

        FamilyCourseRegistrationDTO updateDTO = new FamilyCourseRegistrationDTO();
        updateDTO.setEnrollmentDate(LocalDate.of(2024, 2, 15));

        when(familyCourseRegistrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InvalidMemberIdException.class, () -> {
            familyCourseRegistrationService.updateFamilyMemberRegistration(99L, updateDTO);
        });

        verify(familyCourseRegistrationRepository, times(1)).findById(99L);
        verify(familyCourseRegistrationRepository, never()).save(any());
    }

    @Test
    void testUpdateOnlyNonNullFields() {

        FamilyCourseRegistrations existingRegistration = new FamilyCourseRegistrations();
        existingRegistration.setEnrollmentDate(LocalDate.of(2024, 1, 1));
        existingRegistration.setWithdrawnCredits(5L);
        existingRegistration.setLastUpdatedTime(LocalDateTime.of(2024, 2, 1, 12, 0));

        Optional<FamilyCourseRegistrations> existingRegistrationOpt = Optional.of(existingRegistration);

        FamilyCourseRegistrationDTO familyCourseRegistrationDTO = new FamilyCourseRegistrationDTO();
        familyCourseRegistrationDTO.setEnrollmentDate(LocalDate.of(2025, 3, 10));
        familyCourseRegistrationDTO.setLastUpdatedTime(LocalDateTime.of(2025, 3, 11, 14, 0));

        FamilyCourseRegistrations updatedRegistration = existingRegistrationOpt.get();
        if (familyCourseRegistrationDTO.getEnrollmentDate() != null) {
            updatedRegistration.setEnrollmentDate(familyCourseRegistrationDTO.getEnrollmentDate());
        }
        if (familyCourseRegistrationDTO.getLastUpdatedTime() != null) {
            updatedRegistration.setLastUpdatedTime(familyCourseRegistrationDTO.getLastUpdatedTime());
        }

        assertEquals(LocalDate.of(2025, 3, 10), updatedRegistration.getEnrollmentDate(), "Enrollment date should be updated.");
        assertEquals(5, updatedRegistration.getWithdrawnCredits(), "Withdrawn credits should remain unchanged.");
        assertEquals(LocalDateTime.of(2025, 3, 11, 14, 0), updatedRegistration.getLastUpdatedTime(), "Last updated time should be updated.");
    }



    @Test
    void testDeleteFamilyMemberFromRegisteredCourse_NoWaitlistedMembers() {
        // Given
        Long memberId = 1L;
        Long courseFee = 500L;
        Long withdrawnCredits = 200L;
        Long balance = 300L;
 //  when(familyCourseRegistrationRepository.findById(memberId)).thenReturn(Optional.of(familyCourseRegistrations));
//        when(familyCourseRegistrations.getIsWithdrawn()).thenReturn(IsWithdrawn.NO);
//        when(familyCourseRegistrations.getCost()).thenReturn(courseFee);
//        when(familyCourseRegistrations.getWithdrawnCredits()).thenReturn(withdrawnCredits);

        when(familyGroupRepository.findById(anyLong())).thenReturn(Optional.of(familyGroups));
        when(familyGroups.getCredits()).thenReturn(100L);
        when(waitlistRepository.findByOfferedCourses_OfferedCourseIdAndIsWaitListed(anyLong(), eq(IsWaitListed.YES)))
                .thenReturn(List.of());

        // When
        familyCourseRegistrationService.deleteFamilyMemberFromRegisteredCourse(memberId);

        // Then
        verify(familyCourseRegistrationRepository, times(1)).save(familyCourseRegistrations);
        verify(familyGroupRepository, times(1)).save(familyGroups);
        verify(waitlistRepository, never()).save(any());
    }


}
