//package io.reactivestax.activelife.service;
//
//import io.reactivestax.activelife.Enums.GroupOwner;
//import io.reactivestax.activelife.Enums.PreferredMode;
//import io.reactivestax.activelife.Enums.Status;
//import io.reactivestax.activelife.domain.membership.FamilyGroups;
//import io.reactivestax.activelife.domain.membership.MemberRegistration;
//import io.reactivestax.activelife.domain.membership.Login;
//import io.reactivestax.activelife.dto.MemberRegistrationDTO;
//import io.reactivestax.activelife.dto.LoginDTO;
//import io.reactivestax.activelife.exception.GlobalExceptionHandler;
//import io.reactivestax.activelife.exception.InvalidMemberIdException;
//import io.reactivestax.activelife.exception.ResourceNotFoundException;
//import io.reactivestax.activelife.utility.interfaces.FamilyMemberMapper;
//import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
//import io.reactivestax.activelife.repository.memberregistration.FamilyGroupRepository;
//import io.reactivestax.activelife.repository.memberregistration.LoginRepository;
//import io.reactivestax.activelife.utility.distribution.SmsService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//
//import java.time.LocalDate;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//  class MemberRegistrationServiceTest {
//
//    @InjectMocks
//    private MemberRegistrationService memberRegistrationService;
//
//    @Mock
//    private MemberRegistrationRepository familyMemberRepository;
//
//    @Mock
//    private FamilyGroupRepository familyGroupRepository;
//
//    @Mock
//    private LoginRepository loginRepository;
//
//    @Mock
//    private SmsService smsService;
//
//    @Mock
//    private FamilyMemberMapper familyMemberMapper;
//
//
//
//
//
//
//      private GlobalExceptionHandler globalExceptionHandler;
//
//
//
//    private MemberRegistrationDTO memberRegistrationDTO;
//    private MemberRegistration memberRegistration;
//    private String generatedPin;
//    LocalDate localDate = LocalDate.now();
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        memberRegistrationDTO = new MemberRegistrationDTO();
//        memberRegistrationDTO.setMemberLogin("John Doe");
//        memberRegistrationDTO.setDob(localDate);
//        memberRegistrationDTO.setGender("Male");
//        memberRegistrationDTO.setEmail("john.doe@example.com");
//        memberRegistrationDTO.setStreetNo("123");
//        memberRegistrationDTO.setStreetName("Main St");
//        memberRegistrationDTO.setCity("Springfield");
//        memberRegistrationDTO.setProvince("Illinois");
//        memberRegistrationDTO.setPostalCode("12345");
//        memberRegistrationDTO.setPreferredMode(PreferredMode.SMS);
//        memberRegistrationDTO.setMemberLoginId("john.doe");
//        memberRegistrationDTO.setCountry("USA");
//        memberRegistrationDTO.setHomePhoneNo("5551234567");
//        memberRegistrationDTO.setBussinessPhoneNo("5557654321");
//        memberRegistrationDTO.setLanguage("English");
//
//        memberRegistration = new MemberRegistration();
//
//        generatedPin = memberRegistrationService.generatePin();
//        globalExceptionHandler = new GlobalExceptionHandler();
//    }
//
//    @Test
//    void testSetFamilyMemberDetails() {
//
//        String pin = memberRegistrationService.setFamilyMemberDetails(memberRegistrationDTO, memberRegistration, generatedPin);
//
//        assertEquals("John Doe", memberRegistration.getMemberName());
//        assertEquals(localDate, memberRegistration.getDob());
//        assertEquals("Male", memberRegistration.getGender());
//        assertEquals("john.doe@example.com", memberRegistration.getEmail());
//        assertEquals("123", memberRegistration.getStreetNo());
//        assertEquals("Main St", memberRegistration.getStreetName());
//        assertEquals("Springfield", memberRegistration.getCity());
//        assertEquals("Illinois", memberRegistration.getProvince());
//        assertEquals("12345", memberRegistration.getPostalCode());
//        assertEquals(PreferredMode.SMS, memberRegistration.getPreferredMode());
//        assertEquals("john.doe", memberRegistration.getMemberLogin());
//        assertEquals(generatedPin, memberRegistration.getPin());
//        assertEquals("USA", memberRegistration.getCountry());
//        assertEquals("5551234567", memberRegistration.getHomePhoneNo());
//        assertEquals("5557654321", memberRegistration.getBussinessPhoneNo());
//        assertEquals("English", memberRegistration.getLanguage());
//        assertEquals(Status.INACTIVE, memberRegistration.getStatus());
//
//        assertNotNull(memberRegistration.getVerificationUUID());
//        String verificationLink = "http://localhost:8082/api/familyregistration/verify/" + memberRegistration.getVerificationUUID();
//        verify(smsService, times(1)).sendSms(memberRegistration.getHomePhoneNo(), "Please verify using this link: " + verificationLink,anyString());
//
//        assertEquals(generatedPin, pin);
//    }
//
//    @Test
//    void testAddNewFamilyMemberOnSignup_memberExists() {
//
//        when(familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId()))
//                .thenReturn(Optional.of(memberRegistration));
//
//        InvalidMemberIdException exception = assertThrows(
//                InvalidMemberIdException.class,
//                () -> memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO)
//        );
//        assertEquals("Member Login ID already exists", exception.getMessage());
//    }
//
//    @Test
//    void testGetAllMembersbygivenMemberId_memberNotFound() {
//
//        when(familyMemberRepository.findByMemberLogin("invalidId")).thenReturn(Optional.empty());
//        InvalidMemberIdException exception = assertThrows(
//                InvalidMemberIdException.class,
//                () -> memberRegistrationService.getAllMembersbygivenMemberId("invalidId")
//        );
//
//        assertEquals("This member is not registered", exception.getMessage());
//    }
//
//    @Test
//    void testLoginExistingMember_success() {
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("1");
//        loginDTO.setPin("123456");
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setFamilyMemberId(1L);
//        memberRegistration.setMemberLogin("1");
//        memberRegistration.setPin("123456");
//        memberRegistration.setStatus(Status.ACTIVE);
//        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(memberRegistration));
//
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//
//        assertEquals("OTP sent successfully", result);
//        verify(smsService).sendSms(eq(memberRegistration.getHomePhoneNo()), contains("Your OTP number"),anyString());
//    }
//
//    @Test
//    void testLoginExistingMember_memberInactive() {
//
//        MemberRegistration inactiveFamilyMember = new MemberRegistration();
//        inactiveFamilyMember.setMemberLogin("testLoginId");
//        inactiveFamilyMember.setPin("123456");
//        inactiveFamilyMember.setStatus(Status.INACTIVE);
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("testLoginId");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin("testLoginId")).thenReturn(Optional.of(inactiveFamilyMember));
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//
//        assertEquals("Verification link sent successfully", result);
//        verify(smsService).verificationLink(eq(inactiveFamilyMember.getHomePhoneNo()), contains("http://localhost:8082/api/v1/familymember/verify/"),anyString());
//    }
//
//
//
//    @Test
//    void testDeleteFamilyMemberById_notFound() {
//
//        when(familyMemberRepository.findByMemberLogin(anyString())).thenReturn(Optional.empty());
//
//        NoSuchElementException exception = assertThrows(
//                NoSuchElementException.class,
//                () -> memberRegistrationService.deleteFamilyMemberById("1")
//        );
//
//        assertEquals("No value present", exception.getMessage());
//    }
//
//
//
//    @Test
//    void loginExistingMember_memberNotFound() {
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("nonExistingLogin");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
//                .thenReturn(Optional.empty());
//
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//        assertTrue(result.contains("does not exist"));
//    }
//
//    @Test
//    void loginExistingMember_successfulOtp() {
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("existingLogin");
//        loginDTO.setPin("123456");
//
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin("existingLogin");
//        memberRegistration.setPin("123456");
//        memberRegistration.setStatus(Status.ACTIVE);
//        memberRegistration.setHomePhoneNo("123456789");
//
//        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
//                .thenReturn(Optional.of(memberRegistration));
//
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//        assertEquals("OTP sent successfully", result);
//    }
//
//    @Test
//    void findFamilyMemberByVerificationId_memberNotFound() {
//        when(familyMemberRepository.findByVerificationUUID(anyString()))
//                .thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> {
//            memberRegistrationService.findFamilyMemberByVerificationId("nonExistingVerificationId");
//        });
//    }
//
//    @Test
//    void findFamilyMemberByVerificationId_successful() {
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setStatus(Status.INACTIVE);
//        memberRegistration.setFamilyGroupId(new FamilyGroups());
//
//        when(familyMemberRepository.findByVerificationUUID(anyString()))
//                .thenReturn(Optional.of(memberRegistration));
//
//        memberRegistrationService.findFamilyMemberByVerificationId("validVerificationId");
//
//        assertEquals(Status.ACTIVE, memberRegistration.getStatus());
//    }
//
//    @Test
//    void findFamilyMemberByOtpVerification_memberNotFound() {
//        // Simulating that the member does not exist in the repository
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("nonExistingLogin");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
//                .thenReturn(Optional.empty());
//
//        assertThrows(InvalidMemberIdException.class, () -> {
//            memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
//        });
//    }
//
//    @Test
//    void findFamilyMemberByOtpVerification_memberLoginMismatch() {
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("wrongLogin");
//        loginDTO.setPin("123456");
//
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin("correctLogin");
//
//        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
//                .thenReturn(Optional.of(memberRegistration));
//
//        assertThrows(InvalidMemberIdException.class, () -> {
//            memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
//        });
//    }
//
//    @Test
//    void findFamilyMemberByOtpVerification_pinMismatch() {
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("correctLogin");
//        loginDTO.setPin("wrongPin");
//
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin("correctLogin");
//        memberRegistration.setPin("correctPin");
//
//        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId()))
//                .thenReturn(Optional.of(memberRegistration));
//
//        assertThrows(RuntimeException.class, () -> {
//            memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
//        });
//    }
//
//
//    @Test
//    void createNewFamilyGroup_shouldCreateAndSaveFamilyGroup() {
//        String pin = "123456";
//        FamilyGroups expectedFamilyGroup = new FamilyGroups();
//        expectedFamilyGroup.setFamilyPin(pin);
//        expectedFamilyGroup.setStatus(Status.INACTIVE);
//        expectedFamilyGroup.setCredits(0L);
//        expectedFamilyGroup.setCreatedBy(1L);
//        expectedFamilyGroup.setLastUpdatedBy(1L);
//
//        when(familyGroupRepository.save(any(FamilyGroups.class))).thenReturn(expectedFamilyGroup);
//
//
//        FamilyGroups result = memberRegistrationService.createNewFamilyGroup("111", memberRegistrationDTO);
//
//        verify(familyGroupRepository, times(1)).save(any(FamilyGroups.class));
//        assertNotNull(result);
//        assertNotNull(result.getFamilyPin());
//        assertEquals(Status.INACTIVE, result.getStatus());
//        assertEquals(0L, result.getCredits());
//        assertNotNull(result.getCreatedAt());
//        assertNotNull(result.getUpdatedAt());
//
//    }
//
//
//    @Test
//    void loginExistingMember_memberDoesNotExist() {
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("inactiveMember");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId())).thenReturn(Optional.empty());
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//        assertEquals("Member Login Id does not exist : inactiveMember  Signup please.", result);
//    }
//
//    @Test
//    void loginExistingMember_memberActiveWithCorrectPin() {
//        String memberLoginId = "activeMember";
//        String correctPin = "123456";
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//        memberRegistration.setPin(correctPin);
//        memberRegistration.setStatus(Status.ACTIVE);
//        memberRegistration.setHomePhoneNo("123-456-7890");
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("activeMember");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//        verify(smsService, times(1)).sendSms(eq("123-456-7890"), startsWith("Your OTP number is"),anyString());
//        assertEquals("OTP sent successfully", result);
//    }
//
//    @Test
//    void loginExistingMember_memberInactiveWithIncorrectPin() {
//        String memberLoginId = "inactiveMember";
//        String incorrectPin = "wrongPin";
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//        memberRegistration.setPin("123456");
//        memberRegistration.setStatus(Status.INACTIVE);
//        memberRegistration.setHomePhoneNo("123-456-7890");
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("inactiveMember");
//        loginDTO.setPin("wrongPin");
//
//
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//        InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
//            memberRegistrationService.loginExistingMember(loginDTO);
//        });
//        assertEquals("Password does not match with Member login Id: inactiveMember", exception.getMessage());
//    }
//
//    @Test
//    void loginExistingMember_memberInactiveWithCorrectPin() {
//
//        String memberLoginId = "inactiveMember";
//        String correctPin = "123456";
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//        memberRegistration.setPin(correctPin);
//        memberRegistration.setStatus(Status.INACTIVE);
//        memberRegistration.setHomePhoneNo("123-456-7890");
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("inactiveMember");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//
//        assertEquals("Verification link sent successfully", result);
//    }
//
//    @Test
//    void loginExistingMember_memberActiveWithCorrectPinAndAlreadyVerified() {
//
//        String memberLoginId = "activeMember";
//        String correctPin = "123456";
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//        memberRegistration.setPin(correctPin);
//        memberRegistration.setStatus(Status.ACTIVE);
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("activeMember");
//        loginDTO.setPin("123456");
//
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//
//        assertEquals("OTP sent successfully", result);
//    }
//
//    @Test
//    void updateExistingFamilyMember_memberExists() {
//        String memberLoginId = "existingMember";
//        MemberRegistrationDTO memberRegistrationDTO = new MemberRegistrationDTO();
//        memberRegistrationDTO.setMemberLoginId(memberLoginId);
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//
//        lenient().when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//        memberRegistrationService.updateExistingFamilyMember(memberRegistrationDTO);
//        verify(familyMemberMapper, times(1)).toEntity(memberRegistrationDTO);
//
//    }
//    @Test
//    void getAllMembersbygivenMemberId_memberNotFound() {
//
//        String memberLoginId = "nonExistentMember";
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.empty());
//        InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
//            memberRegistrationService.getAllMembersbygivenMemberId(memberLoginId);
//        });
//
//        assertEquals("This member is not registered", exception.getMessage());
//        verify(familyMemberRepository, times(1)).findByMemberLogin(memberLoginId); // Ensure repository method is called
//    }
//
//    @Test
//    void getAllMembersbygivenMemberId_memberExists() {
//
//        String memberLoginId = "existingMember";
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//
//        MemberRegistrationDTO expectedMemberRegistrationDTO = new MemberRegistrationDTO();
//        expectedMemberRegistrationDTO.setMemberLoginId(memberLoginId);
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//        when(familyMemberMapper.toDto(memberRegistration)).thenReturn(expectedMemberRegistrationDTO);
//
//        MemberRegistrationDTO result = memberRegistrationService.getAllMembersbygivenMemberId(memberLoginId);
//
//        assertNotNull(result);
//        assertEquals(memberLoginId, result.getMemberLoginId());
//        verify(familyMemberRepository, times(1)).findByMemberLogin(memberLoginId); // Ensure repository method is called
//        verify(familyMemberMapper, times(1)).toDto(memberRegistration); // Ensure mapper is called
//    }
//
//    @Test
//    void loginExistingMember_successfulVerification() {
//
//        String memberLoginId = "testLoginId";
//        String pin = "123456";
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId(memberLoginId);
//        loginDTO.setPin(pin);
//
//        MemberRegistration memberRegistration = new MemberRegistration();
//        memberRegistration.setMemberLogin(memberLoginId);
//        memberRegistration.setPin(pin);
//        memberRegistration.setStatus(Status.ACTIVE);
//
//        when(familyMemberRepository.findByMemberLogin(memberLoginId)).thenReturn(Optional.of(memberRegistration));
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//
//        assertEquals("OTP sent successfully", result);
//        verify(familyMemberRepository, times(1)).findByMemberLogin(memberLoginId);
//    }
//
//    @Test
//    void testGeneratePin() {
//        String pin = memberRegistrationService.generatePin();
//        assertNotNull(pin);
//        assertEquals(6, pin.length());
//        assertTrue(pin.matches("\\d{6}"));
//    }
////    @Test
////    void testGenerateOtp() {
////        String otp = memberRegistrationService.generateOtp();
////        assertNotNull(otp);
////        assertEquals(6, otp.length());
////        assertTrue(otp.matches("\\d{6}"));
////    }
//
//    @Test
//    void testFindFamilyMemberByOtpVerification() {
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("1");
//        loginDTO.setPin("654321");
//        MemberRegistration familyMember = new MemberRegistration();
//        familyMember.setMemberLogin("1");
//        familyMember.setOtp("654321");
//        familyMember.setStatus(Status.INACTIVE);
//        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMember));
//        String result = memberRegistrationService.findFamilyMemberByOtpVerification(loginDTO);
//
//        assertEquals("OTP verified", result);
//        assertEquals(Status.ACTIVE, familyMember.getStatus());
//        verify(familyMemberRepository, times(1)).save(familyMember);
//    }
//
//    @Test
//    void testLoginExistingMember_ActiveStatus() {
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("1");
//        loginDTO.setPin("123456");
//
//        MemberRegistration familyMember = new MemberRegistration();
//        familyMember.setFamilyMemberId(1L);
//        familyMember.setMemberLogin("1");
//        familyMember.setPin("123456");
//        familyMember.setStatus(Status.ACTIVE);
//        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMember));
//
//        doNothing().when(smsService).sendSms(anyString(), anyString(),anyString());
//
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//
//        assertEquals("OTP sent successfully", result);
//
//    }
//
//    @Test
//    void testLoginExistingMember_InactiveStatus() {
//
//        LoginDTO loginDTO = new LoginDTO();
//        loginDTO.setMemberLoginId("1");
//        loginDTO.setPin("123456");
//        MemberRegistration familyMember = new MemberRegistration();
//        familyMember.setPin("123456");
//        familyMember.setStatus(Status.INACTIVE);
//        familyMember.setFamilyMemberId(1L);
//        familyMember.setMemberLogin("1");
//        when(familyMemberRepository.findByMemberLogin("1")).thenReturn(Optional.of(familyMember));
//
//
//        doNothing().when(smsService).verificationLink(anyString(), anyString(),anyString());
//        String result = memberRegistrationService.loginExistingMember(loginDTO);
//        assertEquals("Verification link sent successfully", result);
//
//    }
//
//    @Test
//    void testSaveLoginAudit() {
//        FamilyGroups familyGroups = new FamilyGroups();
//        familyGroups.setFamilyGroupId(1L);
//        memberRegistration.setFamilyMemberId(1L);
//        memberRegistration.setMemberLogin("1");
//        memberRegistration.setFamilyGroupId(familyGroups);
//        memberRegistration.setStatus(Status.ACTIVE);
//        memberRegistrationService.saveLoginAudit(memberRegistration);
//
//        verify(loginRepository, times(1)).save(any(Login.class));
//
//        ArgumentCaptor<Login> loginCaptor = ArgumentCaptor.forClass(Login.class);
//        verify(loginRepository).save(loginCaptor.capture());
//
//        Login capturedLogin = loginCaptor.getValue();
//
//        assertNotNull(capturedLogin.getFamilyMember());
//        assertEquals(memberRegistration, capturedLogin.getFamilyMember());
//        assertNotNull(capturedLogin.getLocalDateTime());
//        assertNotNull(capturedLogin.getCreatedBy());
//
//        assertNotNull(capturedLogin.getVerificationUUID());
//    }
//
//    @Test
//    void testAddNewFamilyMemberOnSignup_MemberLoginIdExists() {
//
//        when(familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId()))
//                .thenReturn(Optional.of(memberRegistration));
//
//        InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
//            memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO);
//        });
//
//        assertEquals("Member Login ID already exists", exception.getMessage());
//    }
//
//      @Test
//      void testHandleResourceNotFoundException() {
//          ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
//          ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleResourceNotFoundException(ex, null);
//          assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//          assertEquals("Resource not found", response.getBody().get("message"));
//      }
//
//      @Test
//      void testHandleGlobalException() {
//          Exception ex = new Exception("Generic error");
//          ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGlobalException(ex, null);
//          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//          assertEquals("Internal Server Error", response.getBody().get("error"));
//          assertEquals("Generic error", response.getBody().get("message"));
//      }
//
//      @Test
//      void testHandleHttpMediaTypeNotSupportedException() {
//          HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Unsupported media type");
//          ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleHttpMediaTypeNotSupported(ex, null);
//
//          assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
//          assertEquals("Unsupported Media Type", response.getBody().get("error"));
//          assertEquals("Unsupported media type", response.getBody().get("message"));
//      }
//      @Test
//      void testAddNewFamilyMemberWhenMemberExists() {
//          when(familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId()))
//                  .thenReturn(Optional.of(new MemberRegistration()));
//          InvalidMemberIdException exception = assertThrows(InvalidMemberIdException.class, () -> {
//              memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO);
//          });
//
//          assertEquals("Member Login ID already exists", exception.getMessage());
//      }
//
//      @Test
//      void shouldThrowExceptionIfMemberAlreadyExists() {
//          when(familyMemberRepository.findByMemberLogin("testUser"))
//                  .thenReturn(Optional.of(new MemberRegistration()));
//          assertThrows(NullPointerException.class, () ->
//                  memberRegistrationService.addNewFamilyMemberOnSignup(memberRegistrationDTO));
//      }
//
//      @Test
//      void testVerifyLogin_SuccessfullyVerified() {
//           LoginDTO loginDTO;
//          loginDTO = new LoginDTO();
//          loginDTO.setMemberLoginId("testUser");
//          loginDTO.setPin("1234");
//          memberRegistration = new MemberRegistration();
//          memberRegistration.setMemberLogin("testUser");
//          memberRegistration.setPin("1234");
//          memberRegistration.setStatus(Status.ACTIVE);
//          String result = memberRegistrationService.loginExistingMember(loginDTO);
//          assertEquals("Member Login Id does not exist : testUser  Signup please.", result);
//      }
//
//
//      @Test
//      void testAssignExistingFamilyGroup() {
//
//          MemberRegistrationDTO memberRegistrationDTO = new MemberRegistrationDTO();
//          memberRegistrationDTO.setFamilyGroupId(1L);
//
//          FamilyGroups existingFamilyGroup = new FamilyGroups();
//
//          MemberRegistration memberRegistration = new MemberRegistration();
//
//          when(familyGroupRepository.findById(1L)).thenReturn(Optional.of(existingFamilyGroup));
//
//          if (memberRegistrationDTO.getFamilyGroupId() != null) {
//              Optional<FamilyGroups> existingGroup = familyGroupRepository.findById(memberRegistrationDTO.getFamilyGroupId());
//              if (existingGroup.isPresent()) {
//                  memberRegistration.setFamilyGroupId(existingGroup.get());
//                  memberRegistration.setGroupOwner(GroupOwner.NO);
//              }
//          }
//
//          assertEquals(existingFamilyGroup, memberRegistration.getFamilyGroupId());
//          assertEquals(GroupOwner.NO, memberRegistration.getGroupOwner());
//      }
//
//      @Test
//      void testCreateNewFamilyGroupWhenNotExists() {
//
//          MemberRegistrationDTO memberRegistrationDTO = new MemberRegistrationDTO();
//          memberRegistrationDTO.setFamilyGroupId(1L);
//
//
//          MemberRegistration memberRegistration = new MemberRegistration();
//          FamilyGroups newFamilyGroup = new FamilyGroups();
//
//          when(familyGroupRepository.findById(1L)).thenReturn(Optional.empty());
//          when(memberRegistrationService.createNewFamilyGroup(generatedPin, memberRegistrationDTO)).thenReturn(newFamilyGroup);
//
//          // When
//          if (memberRegistrationDTO.getFamilyGroupId() != null) {
//              Optional<FamilyGroups> existingGroup = familyGroupRepository.findById(memberRegistrationDTO.getFamilyGroupId());
//              if (existingGroup.isEmpty()) {
//                  FamilyGroups createdGroup = memberRegistrationService.createNewFamilyGroup(generatedPin, memberRegistrationDTO);
//                  memberRegistration.setFamilyGroupId(createdGroup);
//                  memberRegistration.setGroupOwner(GroupOwner.YES);
//              }
//          }
//          assertEquals(GroupOwner.YES, memberRegistration.getGroupOwner());
//      }
//
//
//
//
//
//
//
//
//
//
//
//
//}
