package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.utility.interfaces.FamilyMemberMapper;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyGroupRepository;
import io.reactivestax.activelife.repository.memberregistration.LoginRepository;
import io.reactivestax.activelife.utility.distribution.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jms.core.JmsTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MemberRegistrationServiceTest {

    @InjectMocks
    private MemberRegistrationService memberRegistrationService;

    @Mock
    private MemberRegistrationRepository familyMemberRepository;

    @Mock
    private FamilyGroupRepository familyGroupRepository;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private SmsService smsService;

    @Mock
    private FamilyMemberMapper familyMemberMapper;

    @Mock
    private JmsTemplate jmsTemplate;

    private MemberRegistrationDTO memberRegistrationDTO;
    private FamilyMembers familyMembers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        memberRegistrationDTO = new MemberRegistrationDTO();
        memberRegistrationDTO.setMemberLoginId("testLoginId");
        memberRegistrationDTO.setMemberName("John Doe");

        familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin("testLoginId");
        familyMembers.setPin("123456");
        familyMembers.setStatus(Status.ACTIVE);
    }

    @Test
    void testAddNewFamilyMemberOnSignup_memberExists() {

        when(familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId()))
                .thenReturn(Optional.of(familyMembers));

        InvalidMemberIdException exception = assertThrows(
                InvalidMemberIdException.class,
                () -> memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO)
        );

        assertEquals("Member Login ID already exists", exception.getMessage());
    }

    @Test
    void testAddNewFamilyMemberOnSignup_success() {

        when(familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId()))
                .thenReturn(Optional.empty());

        FamilyGroups familyGroup = new FamilyGroups();
        when(familyGroupRepository.findById(anyLong())).thenReturn(Optional.of(familyGroup));

        String pin = memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO);

        assertNotNull(pin);
        assertEquals(6, pin.length());
        verify(familyMemberRepository).save(any(FamilyMembers.class));

    }

    @Test
    void testGetAllMembersbygivenMemberId_memberNotFound() {

        when(familyMemberRepository.findByMemberLogin("invalidId")).thenReturn(Optional.empty());
        InvalidMemberIdException exception = assertThrows(
                InvalidMemberIdException.class,
                () -> memberRegistrationService.getAllMembersbygivenMemberId("invalidId")
        );

        assertEquals("This member is not registered", exception.getMessage());
    }

    @Test
    void testLoginExistingMember_success() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("testLoginId");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin("testLoginId")).thenReturn(Optional.of(familyMembers));

        String result = memberRegistrationService.loginExistingMember(loginDTO);

        assertEquals("OTP sent successfully", result);
        verify(smsService).sendSms(eq(familyMembers.getHomePhoneNo()), contains("Your OTP number"));
    }

    @Test
    void testLoginExistingMember_memberInactive() {

        FamilyMembers inactiveFamilyMember = new FamilyMembers();
        inactiveFamilyMember.setMemberLogin("testLoginId");
        inactiveFamilyMember.setPin("123456");
        inactiveFamilyMember.setStatus(Status.INACTIVE);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("testLoginId");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin("testLoginId")).thenReturn(Optional.of(inactiveFamilyMember));
        String result = memberRegistrationService.loginExistingMember(loginDTO);

        assertEquals("Verification link sent successfully", result);
        verify(smsService).verificationLink(eq(inactiveFamilyMember.getHomePhoneNo()), contains("http://localhost:8082/api/v1/familymember/verify/"));
    }

    @Test
    void testDeleteFamilyMemberById_success() {

        when(familyMemberRepository.findById(anyLong())).thenReturn(Optional.of(familyMembers));
        memberRegistrationService.deleteFamilyMemberById("1");
        assertEquals(Status.INACTIVE, familyMembers.getStatus());
        verify(familyMemberRepository).save(familyMembers);
    }

    @Test
    void testDeleteFamilyMemberById_notFound() {
        when(familyMemberRepository.findById(anyLong())).thenReturn(Optional.empty());
        InvalidMemberIdException exception = assertThrows(
                InvalidMemberIdException.class,
                () -> memberRegistrationService.deleteFamilyMemberById("1")
        );
        assertEquals("Family member not found", exception.getMessage());
    }

    @Test
    void loginExistingMember_memberNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("nonExistingLogin");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
                .thenReturn(Optional.empty());

        String result = memberRegistrationService.loginExistingMember(loginDTO);
        assertTrue(result.contains("does not exist"));
    }

    @Test
    void loginExistingMember_successfulOtp() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("existingLogin");
        loginDTO.setPin("123456");

        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin("existingLogin");
        familyMembers.setPin("123456");
        familyMembers.setStatus(Status.ACTIVE);
        familyMembers.setHomePhoneNo("123456789");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
                .thenReturn(Optional.of(familyMembers));

        String result = memberRegistrationService.loginExistingMember(loginDTO);
        assertEquals("OTP sent successfully", result);
    }

    @Test
    void findFamilyMemberByVerificationId_memberNotFound() {
        when(familyMemberRepository.findByVerificationUUID(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            memberRegistrationService.findFamilyMemberByVerificationId("nonExistingVerificationId");
        });
    }

    @Test
    void findFamilyMemberByVerificationId_successful() {
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setStatus(Status.INACTIVE);
        familyMembers.setFamilyGroupId(new FamilyGroups());

        when(familyMemberRepository.findByVerificationUUID(anyString()))
                .thenReturn(Optional.of(familyMembers));

        memberRegistrationService.findFamilyMemberByVerificationId("validVerificationId");

        assertEquals(Status.ACTIVE, familyMembers.getStatus());
    }

    @Test
    void findFamilyMemberByOtpVerification_memberNotFound() {
        // Simulating that the member does not exist in the repository
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("nonExistingLogin");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidMemberIdException.class, () -> {
            memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
        });
    }

    @Test
    void findFamilyMemberByOtpVerification_memberLoginMismatch() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("wrongLogin");
        loginDTO.setPin("123456");

        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin("correctLogin");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
                .thenReturn(Optional.of(familyMembers));

        assertThrows(InvalidMemberIdException.class, () -> {
            memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
        });
    }

    @Test
    void findFamilyMemberByOtpVerification_pinMismatch() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("correctLogin");
        loginDTO.setPin("wrongPin");

        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin("correctLogin");
        familyMembers.setPin("correctPin");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
                .thenReturn(Optional.of(familyMembers));

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
        });
    }

    @Test
    void findFamilyMemberByOtpVerification_successfulOtpVerification() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("correctLogin");
        loginDTO.setPin("correctPin");

        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin("correctLogin");
        familyMembers.setPin("correctPin");
        familyMembers.setStatus(Status.INACTIVE);

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
                .thenReturn(Optional.of(familyMembers));

        String result = memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
        assertEquals(Status.ACTIVE, familyMembers.getStatus());
        assertEquals("OTP verified", result);
        verify(familyMemberRepository, times(1)).save(familyMembers);
    }

    @Test
    void createNewFamilyGroup_shouldCreateAndSaveFamilyGroup() {

        String pin = "123456";
        FamilyGroups expectedFamilyGroup = new FamilyGroups();
        expectedFamilyGroup.setFamilyPin(pin);
        expectedFamilyGroup.setStatus(Status.INACTIVE);
        expectedFamilyGroup.setCredits(0L);
        expectedFamilyGroup.setCreatedBy(1L);
        expectedFamilyGroup.setLastUpdatedBy(1L);

        when(familyGroupRepository.save(any(FamilyGroups.class))).thenReturn(expectedFamilyGroup);

        FamilyGroups result = memberRegistrationService.createNewFamilyGroup("111", memberRegistrationDTO);

        verify(familyGroupRepository, times(1)).save(any(FamilyGroups.class));
        assertNotNull(result);
        assertEquals(pin, result.getFamilyPin());
        assertEquals(Status.INACTIVE, result.getStatus());
        assertEquals(0L, result.getCredits());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(1L, result.getCreatedBy());
        assertEquals(1L, result.getLastUpdatedBy());
    }

    @Test
    void loginExistingMember_memberDoesNotExist() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("inactiveMember");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId())).thenReturn(Optional.empty());
        String result = memberRegistrationService.loginExistingMember(loginDTO);
        assertEquals("Member Login Id does not exist: inactiveMember", result);
    }

    @Test
    void loginExistingMember_memberActiveWithCorrectPin() {
        String memberLoginId = "activeMember";
        String correctPin = "123456";
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);
        familyMembers.setPin(correctPin);
        familyMembers.setStatus(Status.ACTIVE);
        familyMembers.setHomePhoneNo("123-456-7890");

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("activeMember");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));
        String result = memberRegistrationService.loginExistingMember(loginDTO);
        verify(smsService, times(1)).sendSms(eq("123-456-7890"), startsWith("Your OTP number is"));
        assertEquals("OTP sent successfully", result);
    }

    @Test
    void loginExistingMember_memberInactiveWithIncorrectPin() {
        String memberLoginId = "inactiveMember";
        String incorrectPin = "wrongPin";
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);
        familyMembers.setPin("123456");
        familyMembers.setStatus(Status.INACTIVE);
        familyMembers.setHomePhoneNo("123-456-7890");

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("inactiveMember");
        loginDTO.setPin("wrongPin");


        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));
        InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
            memberRegistrationService.loginExistingMember(loginDTO);
        });
        assertEquals("Password does not match with Member login Id: inactiveMember", exception.getMessage());
    }

    @Test
    void loginExistingMember_memberInactiveWithCorrectPin() {

        String memberLoginId = "inactiveMember";
        String correctPin = "123456";
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);
        familyMembers.setPin(correctPin);
        familyMembers.setStatus(Status.INACTIVE);
        familyMembers.setHomePhoneNo("123-456-7890");

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("inactiveMember");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));

        String result = memberRegistrationService.loginExistingMember(loginDTO);

        assertEquals("Verification link sent successfully", result);
    }

    @Test
    void loginExistingMember_memberActiveWithCorrectPinAndAlreadyVerified() {

        String memberLoginId = "activeMember";
        String correctPin = "123456";
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);
        familyMembers.setPin(correctPin);
        familyMembers.setStatus(Status.ACTIVE);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("activeMember");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));

        String result = memberRegistrationService.loginExistingMember(loginDTO);

        assertEquals("OTP sent successfully", result);
    }

    @Test
    void updateExistingFamilyMember_memberExists() {
        String memberLoginId = "existingMember";
        MemberRegistrationDTO memberRegistrationDTO = new MemberRegistrationDTO();
        memberRegistrationDTO.setMemberLoginId(memberLoginId);
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);

        lenient().when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));
        memberRegistrationService.updateExistingFamilyMember(memberRegistrationDTO);
        verify(familyMemberMapper, times(1)).toEntity(memberRegistrationDTO);

    }
    @Test
    void getAllMembersbygivenMemberId_memberNotFound() {

        String memberLoginId = "nonExistentMember";
        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.empty());
        InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
            memberRegistrationService.getAllMembersbygivenMemberId(memberLoginId);
        });

        assertEquals("This member is not registered", exception.getMessage());
        verify(familyMemberRepository, times(1)).findByMemberLogin(memberLoginId); // Ensure repository method is called
    }

    @Test
    void getAllMembersbygivenMemberId_memberExists() {

        String memberLoginId = "existingMember";
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);

        MemberRegistrationDTO expectedMemberRegistrationDTO = new MemberRegistrationDTO();
        expectedMemberRegistrationDTO.setMemberLoginId(memberLoginId);
        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));
        when(familyMemberMapper.toDto(familyMembers)).thenReturn(expectedMemberRegistrationDTO);

        MemberRegistrationDTO result = memberRegistrationService.getAllMembersbygivenMemberId(memberLoginId);

        assertNotNull(result);
        assertEquals(memberLoginId, result.getMemberLoginId());
        verify(familyMemberRepository, times(1)).findByMemberLogin(memberLoginId); // Ensure repository method is called
        verify(familyMemberMapper, times(1)).toDto(familyMembers); // Ensure mapper is called
    }

    @Test
    void addNewFamilyMemberOnSignup_familyGroupDoesNotExist() {

        String loginId = "newLoginId";
        Long familyGroupId = 1L;
        MemberRegistrationDTO memberRegistrationDTO = new MemberRegistrationDTO();
        memberRegistrationDTO.setMemberLoginId(loginId);
        memberRegistrationDTO.setFamilyGroupId(familyGroupId);

        FamilyMembers newFamilyMember = new FamilyMembers();

        when(familyMemberRepository.findByMemberLogin(loginId)).thenReturn(Optional.empty());
        when(familyGroupRepository.findById(familyGroupId)).thenReturn(Optional.empty());

        String result = memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO);


        assertNotNull(result);
        assertEquals(6, result.length());
        verify(familyMemberRepository, times(1)).findByMemberLogin(loginId);
        verify(familyGroupRepository, times(1)).findById(familyGroupId);
        verify(familyMemberRepository, times(1)).save(any(FamilyMembers.class));
    }
    @Test
    void loginExistingMember_successfulVerification() {

        String memberLoginId = "testLoginId";
        String pin = "123456";
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId(memberLoginId);
        loginDTO.setPin(pin);

        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setMemberLogin(memberLoginId);
        familyMembers.setPin(pin);
        familyMembers.setStatus(Status.ACTIVE);

        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(familyMembers));
        String result = memberRegistrationService.loginExistingMember(loginDTO);

        assertEquals("OTP sent successfully", result);
        verify(familyMemberRepository, times(1)).findByMemberLogin(memberLoginId);
    }
}
