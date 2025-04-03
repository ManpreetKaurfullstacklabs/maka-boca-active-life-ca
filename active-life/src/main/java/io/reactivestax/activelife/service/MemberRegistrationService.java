package io.reactivestax.activelife.service;


import io.reactivestax.activelife.Enums.GroupOwner;
import io.reactivestax.activelife.Enums.Role;
import io.reactivestax.activelife.Enums.Status;

import io.reactivestax.activelife.domain.membership.Login;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.exception.InvalidMemberIdException;

import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyGroupRepository;
import io.reactivestax.activelife.repository.memberregistration.LoginRepository;
import io.reactivestax.activelife.utility.distribution.SmsService;
import io.reactivestax.activelife.utility.interfaces.FamilyMemberMapper;
import io.reactivestax.activelife.utility.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class MemberRegistrationService {

    @Autowired
    private MemberRegistrationRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private final RestTemplate restTemplate ;


    @Transactional
    public String addNewFamilyMemberOnSignup(MemberRegistrationDTO memberRegistrationDTO) {
        Optional<MemberRegistration> existingFamilyMember = familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId());
        if (existingFamilyMember.isPresent()) {
            throw new InvalidMemberIdException("Member Login ID already exists");
        }

        MemberRegistration memberRegistration = new MemberRegistration();
        String pin = generatePin();
        String encodedPin = passwordEncoder.encode(pin);

        FamilyGroups familyGroup;
        if (memberRegistrationDTO.getFamilyGroupId() != null) {
            Optional<FamilyGroups> existingFamilyGroup = familyGroupRepository.findById(memberRegistrationDTO.getFamilyGroupId());
            if (existingFamilyGroup.isPresent()) {
                familyGroup = existingFamilyGroup.get();
                memberRegistration.setGroupOwner(GroupOwner.NO);
            } else {
                familyGroup = createNewFamilyGroup(encodedPin, memberRegistrationDTO);
                memberRegistration.setGroupOwner(GroupOwner.YES);
            }
        } else {
            familyGroup = createNewFamilyGroup(encodedPin, memberRegistrationDTO);
            memberRegistration.setGroupOwner(GroupOwner.YES);
        }

        memberRegistration.setFamilyGroupId(familyGroup);
        setFamilyMemberDetails(memberRegistrationDTO, memberRegistration, encodedPin);
        familyMemberRepository.save(memberRegistration);
        saveLoginAudit(memberRegistration);

        return pin;
    }


    public MemberRegistrationDTO getAllMembersbygivenMemberId(String id) {
        Optional<MemberRegistration> byMemberLogin = familyMemberRepository.findByMemberLogin(id);
        if (byMemberLogin.isPresent()) {
            MemberRegistration memberRegistration = byMemberLogin.get();
            return familyMemberMapper.toDto(memberRegistration);
        } else {
            throw new InvalidMemberIdException("This member is not registered");
        }
    }
    public void updateExistingFamilyMember(MemberRegistrationDTO memberRegistrationDTO) {
        Optional<MemberRegistration> byId = familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId());
        MemberRegistration memberRegistration = byId.orElseThrow(() -> new InvalidMemberIdException("Member not found"));
        familyMemberMapper.toEntity(memberRegistrationDTO);
        familyMemberRepository.save(memberRegistration);
    }

    public void deleteFamilyMemberById(String id) {
        MemberRegistration memberRegistration = familyMemberRepository.findByMemberLogin(id).get();
        memberRegistration.setStatus(Status.INACTIVE);
        familyMemberRepository.save(memberRegistration);
    }

    public void saveLoginAudit(MemberRegistration memberRegistration) {
        Login login = new Login();
        login.setFamilyMember(memberRegistration);
        login.setLocalDateTime(LocalDateTime.now());
        login.setRole(memberRegistration.getRole());
        login.setCreatedBy(memberRegistration.getFamilyGroupId().getFamilyGroupId().toString());
        login.setFamilyPin(memberRegistration.getPin());
        String verificationId = UUID.randomUUID().toString();
        login.setVerificationUUID(verificationId);
        loginRepository.save(login);
    }

    public void findFamilyMemberByVerificationId(String verificationId) {
        Optional<MemberRegistration> byVerificationId = familyMemberRepository.findByVerificationUUID(verificationId);
        MemberRegistration memberRegistration = byVerificationId.get();
        FamilyGroups familyGroupId = byVerificationId.get().getFamilyGroupId();
        memberRegistration.setStatus(Status.ACTIVE);
        familyGroupId.setStatus(Status.ACTIVE);
        familyMemberRepository.save(memberRegistration);
    }


    public ResponseEntity<String> loginExistingMember(LoginDTO loginDTO) {
        Optional<MemberRegistration> byMemberLoginId = familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId());

        if (byMemberLoginId.isEmpty()) {
            return new ResponseEntity<>("Member Login Id does not exist: " + loginDTO.getMemberLoginId() + "  Signup please.", HttpStatus.BAD_REQUEST);
        }

        MemberRegistration memberRegistration = byMemberLoginId.get();
        if (memberRegistration.getMemberLogin().equals(loginDTO.getMemberLoginId())) {

            if (passwordEncoder.matches(loginDTO.getPin(), memberRegistration.getPin()) && memberRegistration.getStatus().equals(Status.ACTIVE)) {
                smsService.sendOtpRequest(memberRegistration.getHomePhoneNo(), loginDTO.getMemberLoginId());
                familyMemberRepository.save(memberRegistration);
                return new ResponseEntity<>("OTP sent successfully", HttpStatus.OK);
            }

            if (passwordEncoder.matches(loginDTO.getPin(), memberRegistration.getPin()) && memberRegistration.getStatus().equals(Status.INACTIVE)) {
                String verificationId = UUID.randomUUID().toString();
                memberRegistration.setVerificationUUID(verificationId);
                String verificationLink = "http://localhost:40015/api/v1/familymember/verify/" + verificationId;
                smsService.verificationLink(memberRegistration.getHomePhoneNo(), verificationLink, loginDTO.getMemberLoginId());
                return new ResponseEntity<>("Verification link sent successfully", HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }


    public String setFamilyMemberDetails(MemberRegistrationDTO memberRegistrationDTO, MemberRegistration memberRegistration, String encodedPin) {
        memberRegistration.setMemberName(memberRegistrationDTO.getMemberName());
        memberRegistration.setDob(memberRegistrationDTO.getDob());
        memberRegistration.setGender(memberRegistrationDTO.getGender());
        memberRegistration.setEmail(memberRegistrationDTO.getEmail());
        memberRegistration.setStreetNo(memberRegistrationDTO.getStreetNo());
        memberRegistration.setStreetName(memberRegistrationDTO.getStreetName());
        memberRegistration.setCity(memberRegistrationDTO.getCity());
        memberRegistration.setProvince(memberRegistrationDTO.getProvince());
        memberRegistration.setPostalCode(memberRegistrationDTO.getPostalCode());
        memberRegistration.setPreferredMode(memberRegistrationDTO.getPreferredMode());
        memberRegistration.setMemberLogin(memberRegistrationDTO.getMemberLoginId());
        memberRegistration.setRole(Role.USER);
        memberRegistration.setPin(encodedPin);
        memberRegistration.setCountry(memberRegistrationDTO.getCountry());
        memberRegistration.setHomePhoneNo(memberRegistrationDTO.getHomePhoneNo());
        memberRegistration.setBussinessPhoneNo(memberRegistrationDTO.getBussinessPhoneNo());
        memberRegistration.setLanguage(memberRegistrationDTO.getLanguage());
        memberRegistration.setStatus(Status.INACTIVE);
        String verificationId = UUID.randomUUID().toString();
        memberRegistration.setVerificationUUID(verificationId);
        String verificationLink = "http://localhost:40015/api/familyregistration/verify/"+ verificationId;
        smsService.sendSms(memberRegistration.getHomePhoneNo(), "Please verify using this link: " + verificationLink,memberRegistrationDTO.getMemberLoginId());
        return  encodedPin;
    }

    public FamilyGroups createNewFamilyGroup(String encodedpin , MemberRegistrationDTO memberRegistrationDTO) {

        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyPin(encodedpin);
        familyGroups.setStatus(Status.INACTIVE);
        familyGroups.setCredits(0L);
        familyGroups.setCreatedAt(LocalDateTime.now());
        familyGroups.setUpdatedAt(LocalDateTime.now());
        familyGroups.setCreatedBy(memberRegistrationDTO.getFamilyGroupId());
        familyGroups.setLastUpdatedBy(familyGroups.getFamilyGroupId());
        familyGroupRepository.save(familyGroups);
        return familyGroups;
    }


    public String findFamilyMemberByOtpVerification(LoginDTO loginDTO) {
        Optional<MemberRegistration> familyMemberOpt = familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId());

        if (!familyMemberOpt.isPresent()) {
            throw new InvalidMemberIdException("This member does not exist");
        }

        MemberRegistration memberRegistration = familyMemberOpt.get();

        String otpVerificationUrl = "http://localhost:8082/api/v1/otp/verify";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("customerId", loginDTO.getMemberLoginId());
        requestBody.put("otpNo", loginDTO.getPin());

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    otpVerificationUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return jwtUtil.generateToken(loginDTO.getMemberLoginId(), loginDTO.getPin());
            } else {
                throw new RuntimeException("OTP verification failed with status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("OTP does not exist or has expired. Please request a new OTP.");
        } catch (Exception e) {
            throw new RuntimeException("Error calling OTP verification API: " + e.getMessage());
        }
    }






    public String generatePin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }


}
