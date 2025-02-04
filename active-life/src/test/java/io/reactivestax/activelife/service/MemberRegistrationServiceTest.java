package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.PreferredMode;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.domain.membership.Login;
import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.exception.GlobalExceptionHandler;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.exception.ResourceNotFoundException;
import io.reactivestax.activelife.utility.interfaces.FamilyMemberMapper;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyGroupRepository;
import io.reactivestax.activelife.repository.memberregistration.LoginRepository;
import io.reactivestax.activelife.utility.distribution.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

  class MemberRegistrationServiceTest {

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

      private GlobalExceptionHandler globalExceptionHandler;



    private MemberRegistrationDTO memberRegistrationDTO;
    private FamilyMembers familyMembers;
    private String generatedPin;
    LocalDate localDate = LocalDate.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        memberRegistrationDTO = new MemberRegistrationDTO();
        memberRegistrationDTO.setMemberName("John Doe");
        memberRegistrationDTO.setDob(localDate);
        memberRegistrationDTO.setGender("Male");
        memberRegistrationDTO.setEmail("john.doe@example.com");
        memberRegistrationDTO.setStreetNo("123");
        memberRegistrationDTO.setStreetName("Main St");
        memberRegistrationDTO.setCity("Springfield");
        memberRegistrationDTO.setProvince("Illinois");
        memberRegistrationDTO.setPostalCode("12345");
        memberRegistrationDTO.setPreferredMode(PreferredMode.SMS);
        memberRegistrationDTO.setMemberLoginId("john.doe");
        memberRegistrationDTO.setCountry("USA");
        memberRegistrationDTO.setHomePhoneNo("5551234567");
        memberRegistrationDTO.setBussinessPhoneNo("5557654321");
        memberRegistrationDTO.setLanguage("English");

        familyMembers = new FamilyMembers();

        generatedPin = memberRegistrationService.generatePin();
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testSetFamilyMemberDetails() {

        String pin = memberRegistrationService.setFamilyMemberDetails(memberRegistrationDTO, familyMembers, generatedPin);

        assertEquals("John Doe", familyMembers.getMemberName());
        assertEquals(localDate, familyMembers.getDob());
        assertEquals("Male", familyMembers.getGender());
        assertEquals("john.doe@example.com", familyMembers.getEmail());
        assertEquals("123", familyMembers.getStreetNo());
        assertEquals("Main St", familyMembers.getStreetName());
        assertEquals("Springfield", familyMembers.getCity());
        assertEquals("Illinois", familyMembers.getProvince());
        assertEquals("12345", familyMembers.getPostalCode());
        assertEquals(PreferredMode.SMS, familyMembers.getPreferredMode());
        assertEquals("john.doe", familyMembers.getMemberLogin());
        assertEquals(generatedPin, familyMembers.getPin());
        assertEquals("USA", familyMembers.getCountry());
        assertEquals("5551234567", familyMembers.getHomePhoneNo());
        assertEquals("5557654321", familyMembers.getBussinessPhoneNo());
        assertEquals("English", familyMembers.getLanguage());
        assertEquals(Status.INACTIVE, familyMembers.getStatus());

        assertNotNull(familyMembers.getVerificationUUID());
        String verificationLink = "http://localhost:8082/api/familyregistration/verify/" + familyMembers.getVerificationUUID();
        verify(smsService, times(1)).sendSms(familyMembers.getHomePhoneNo(), "Please verify using this link: " + verificationLink);

        assertEquals(generatedPin, pin);
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
        loginDTO.setMemberLoginId("1");
        loginDTO.setPin("123456");
        FamilyMembers familyMembers = new FamilyMembers();
        familyMembers.setFamilyMemberId(1L);
        familyMembers.setMemberLogin("1");
        familyMembers.setPin("123456");
        familyMembers.setStatus(Status.ACTIVE);
        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMembers));

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
    void testDeleteFamilyMemberById_notFound() {

        when(familyMemberRepository.findByMemberLogin(anyString())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> memberRegistrationService.deleteFamilyMemberById("1")
        );

        assertEquals("No value present", exception.getMessage());
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
        assertNotNull(result.getFamilyPin());
        assertEquals(Status.INACTIVE, result.getStatus());
        assertEquals(0L, result.getCredits());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

    }


    @Test
    void loginExistingMember_memberDoesNotExist() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("inactiveMember");
        loginDTO.setPin("123456");

        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId())).thenReturn(Optional.empty());
        String result = memberRegistrationService.loginExistingMember(loginDTO);
        assertEquals("Member Login Id does not exist : inactiveMember  Signup please.", result);
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

    @Test
    void testGeneratePin() {
        String pin = memberRegistrationService.generatePin();
        assertNotNull(pin);
        assertEquals(6, pin.length());
        assertTrue(pin.matches("\\d{6}"));
    }
    @Test
    void testGenerateOtp() {
        String otp = memberRegistrationService.generateOtp();
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }

    @Test
    void testFindFamilyMemberByOtpVerification() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("1");
        loginDTO.setPin("654321");


        FamilyMembers familyMember = new FamilyMembers();
        familyMember.setMemberLogin("1");
        familyMember.setOtp("654321");
        familyMember.setStatus(Status.INACTIVE);

        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMember));

        String result = memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);

        assertEquals("OTP verified", result);
        assertEquals(Status.ACTIVE, familyMember.getStatus());
        verify(familyMemberRepository, times(1)).save(familyMember);
    }

    @Test
    void testLoginExistingMember_ActiveStatus() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("1");
        loginDTO.setPin("123456");

        FamilyMembers familyMember = new FamilyMembers();
        familyMember.setFamilyMemberId(1L);
        familyMember.setMemberLogin("1");
        familyMember.setPin("123456");
        familyMember.setStatus(Status.ACTIVE);
        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMember));

        doNothing().when(smsService).sendSms(anyString(), anyString());

        String result = memberRegistrationService.loginExistingMember(loginDTO);

        assertEquals("OTP sent successfully", result);

    }

    @Test
    void testLoginExistingMember_InactiveStatus() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("1");
        loginDTO.setPin("123456");


        FamilyMembers familyMember = new FamilyMembers();
        familyMember.setPin("123456");
        familyMember.setStatus(Status.INACTIVE);
        familyMember.setFamilyMemberId(1L);
        familyMember.setMemberLogin("1");
        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMember));


        doNothing().when(smsService).verificationLink(anyString(), anyString());


        String result = memberRegistrationService.loginExistingMember(loginDTO);


        assertEquals("Verification link sent successfully", result);

    }

    @Test
    void testSaveLoginAudit() {
        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyGroupId(1L);
        familyMembers.setFamilyMemberId(1L);
        familyMembers.setMemberLogin("1");
        familyMembers.setFamilyGroupId(familyGroups);
        familyMembers.setStatus(Status.ACTIVE);
        memberRegistrationService.saveLoginAudit(familyMembers);

        verify(loginRepository, times(1)).save(any(Login.class));

        ArgumentCaptor<Login> loginCaptor = ArgumentCaptor.forClass(Login.class);
        verify(loginRepository).save(loginCaptor.capture());

        Login capturedLogin = loginCaptor.getValue();

        assertNotNull(capturedLogin.getFamilyMember());
        assertEquals(familyMembers, capturedLogin.getFamilyMember());
        assertNotNull(capturedLogin.getLocalDateTime());
        assertNotNull(capturedLogin.getCreatedBy());

        assertNotNull(capturedLogin.getVerificationUUID());
    }

    @Test
    void testAddNewFamilyMemberOnSignup_MemberLoginIdExists() {

        when(familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId()))
                .thenReturn(Optional.of(familyMembers));

        InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
            memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO);
        });

        assertEquals("Member Login ID already exists", exception.getMessage());
    }

      @Test
      void testHandleResourceNotFoundException() {
          ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
          ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleResourceNotFoundException(ex, null);

          assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
          assertEquals("Resource not found", response.getBody().get("message"));
      }

      @Test
      void testHandleGlobalException() {
          Exception ex = new Exception("Generic error");
          ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGlobalException(ex, null);

          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
          assertEquals("Internal Server Error", response.getBody().get("error"));
          assertEquals("Generic error", response.getBody().get("message"));
      }

      @Test
      void testHandleHttpMediaTypeNotSupportedException() {
          HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Unsupported media type");
          ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMediaTypeNotSupported(ex, null);

          assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
          assertEquals("Unsupported Media Type", response.getBody().get("error"));
          assertEquals("Unsupported media type", response.getBody().get("message"));
      }






}
