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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
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


    @Transactional
    public String addNewFamilyMemberOnSignup(MemberRegistrationDTO memberRegistrationDTO) {
        Optional<MemberRegistration> existingFamilyMember = familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId());
        if (existingFamilyMember.isPresent()) {
            throw new InvalidMemberIdException("Member Login ID already exists");
        }
        MemberRegistration memberRegistration = new MemberRegistration();
        String pin = generatePin();
        String encodedPin = passwordEncoder.encode(pin);
        if (memberRegistrationDTO.getFamilyGroupId() != null) {
            Optional<FamilyGroups> existingFamilyGroup = familyGroupRepository.findById(memberRegistrationDTO.getFamilyGroupId());
            if (existingFamilyGroup.isPresent()) {
                memberRegistration.setFamilyGroupId(existingFamilyGroup.get());
                memberRegistration.setGroupOwner(GroupOwner.NO);
            } else {
                FamilyGroups newFamilyGroup = createNewFamilyGroup(encodedPin, memberRegistrationDTO);
                memberRegistration.setFamilyGroupId(newFamilyGroup);
                memberRegistration.setGroupOwner(GroupOwner.YES);
            }
        }
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

    public String loginExistingMember(LoginDTO loginDTO) {
        Optional<MemberRegistration> byMemberLoginId = familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId());
        if (byMemberLoginId.isEmpty()) {
            return "Member Login Id does not exist : " + loginDTO.getMemberLoginId()+"  Signup please.";
        }
        MemberRegistration memberRegistration = byMemberLoginId.get();
        if (memberRegistration.getMemberLogin().equals(loginDTO.getMemberLoginId())) {
            if(passwordEncoder.matches(loginDTO.getPin(),memberRegistration.getPin())&& memberRegistration.getStatus().equals(Status.ACTIVE)){
                // if (memberRegistration.getPin().equals(loginDTO.getPin()) && memberRegistration.getStatus().equals(Status.ACTIVE)) {
              //  String otp = generateOtp();
                smsService.sendOtpRequest(memberRegistration.getHomePhoneNo(), loginDTO.getMemberLoginId());
              //  smsService.sendSms(memberRegistration.getHomePhoneNo(), "Your OTP number is " + otp,loginDTO.getMemberLoginId());
               // memberRegistration.setOtp(otp);
                familyMemberRepository.save(memberRegistration);
                return "OTP sent successfully";
            }
        }

        if(passwordEncoder.matches(loginDTO.getPin(),memberRegistration.getPin())&& memberRegistration.getStatus().equals(Status.INACTIVE)){
            //    if (memberRegistration.getMemberLogin().equals(loginDTO.getMemberLoginId()) && memberRegistration.getStatus().equals(Status.INACTIVE)) {
            //    if (!memberRegistration.getPin().equals(loginDTO.getPin())) {
            if (!passwordEncoder.matches(loginDTO.getPin(),memberRegistration.getPin())) {
                throw new InvalidMemberIdException("Password does not match with Member login Id: " + memberRegistration.getMemberLogin());
            } else {
                String verificationId = UUID.randomUUID().toString();
                memberRegistration.setVerificationUUID(verificationId);
                String verificationLink = "http://localhost:8082/api/v1/familymember/verify/" + verificationId;
                smsService.verificationLink(memberRegistration.getHomePhoneNo(), verificationLink, loginDTO.getMemberLoginId());
                return "Verification link sent successfully";
            }
        }
        if (loginDTO.getMemberLoginId().equals(memberRegistration.getMemberLogin()) && loginDTO.getPin().equals(memberRegistration.getPin()) && memberRegistration.getStatus().equals(Status.ACTIVE)) {
            return "Successfully verified";
        }
        return "Invalid " ;
    }

//    public String generateOtp() {
//        Random random = new Random();
//        StringBuilder otp = new StringBuilder();
//        for (int i = 0; i < 6; i++) {
//            otp.append(random.nextInt(10));
//        }
//        return otp.toString();
//    }

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
        String verificationLink = "http://localhost:8080/api/familyregistration/verify/"+ verificationId;
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

        if (familyMemberOpt.isEmpty()) {
            throw new InvalidMemberIdException("This member does not exist");
        }

        MemberRegistration memberRegistration = familyMemberOpt.get();

        if (!memberRegistration.getOtp().equals(loginDTO.getPin())) {
            throw new RuntimeException("Wrong OTP");
        }
        memberRegistration.setStatus(Status.ACTIVE);
        familyMemberRepository.save(memberRegistration);
        String token = jwtUtil.generateToken(loginDTO.getMemberLoginId(), loginDTO.getPin());
        return token;
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
