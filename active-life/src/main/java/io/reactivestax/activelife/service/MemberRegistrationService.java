package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.GroupOwner;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.utility.distribution.SmsService;
import io.reactivestax.activelife.domain.membership.Login;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.MemberRegistrationDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.utility.interfaces.FamilyMemberMapper;
import io.reactivestax.activelife.repository.familymember.FamilMemberRepository;
import io.reactivestax.activelife.repository.familymember.FamilyGroupRepository;
import io.reactivestax.activelife.repository.login.LoginRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class MemberRegistrationService {

    @Autowired
    private FamilMemberRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public String addNewFamilyMemberOnSignup(MemberRegistrationDTO memberRegistrationDTO) {

        Optional<FamilyMembers> existingFamilyMember = familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId());
        if (existingFamilyMember.isPresent()) {
            throw new InvalidMemberIdException("Member Login ID already exists");
        }
        FamilyMembers familyMembers = new FamilyMembers();
        String pin = generatePin();
        if (memberRegistrationDTO.getFamilyGroupId() != null) {
            Optional<FamilyGroups> existingFamilyGroup = familyGroupRepository.findById(memberRegistrationDTO.getFamilyGroupId());
            if (existingFamilyGroup.isPresent()) {
                familyMembers.setFamilyGroupId(existingFamilyGroup.get());
                familyMembers.setGroupOwner(GroupOwner.NO);
            } else {
                FamilyGroups newFamilyGroup = createNewFamilyGroup(pin, memberRegistrationDTO);
                familyMembers.setFamilyGroupId(newFamilyGroup);
                familyMembers.setGroupOwner(GroupOwner.YES);
            }
        }
        setFamilyMemberDetails(memberRegistrationDTO, familyMembers, pin);
        familyMemberRepository.save(familyMembers);
        saveLoginAudit(familyMembers);

        return pin;
    }

    public MemberRegistrationDTO getAllMembersbygivenMemberId(String id) {
        Optional<FamilyMembers> byMemberLogin = familyMemberRepository.findByMemberLogin(id);
        if (byMemberLogin.isPresent()) {
            FamilyMembers familyMembers = byMemberLogin.get();
            return familyMemberMapper.toDto(familyMembers);
        } else {
            throw new InvalidMemberIdException("This member is not registered");
        }
    }
    public void updateExistingFamilyMember(MemberRegistrationDTO memberRegistrationDTO) {
        Optional<FamilyMembers> byId = familyMemberRepository.findByMemberLogin(memberRegistrationDTO.getMemberLoginId());
        FamilyMembers familyMembers = byId.orElseThrow(() -> new InvalidMemberIdException("Member not found"));
        familyMemberMapper.toEntity(memberRegistrationDTO);
        familyMemberRepository.save(familyMembers);
    }

    public void deleteFamilyMemberById(String id) {
        FamilyMembers familyMembers = familyMemberRepository.findByMemberLogin(id).get();
        familyMembers.setStatus(Status.INACTIVE);
        familyMemberRepository.save(familyMembers);
    }

    private void saveLoginAudit(FamilyMembers familyMembers) {
        Login login = new Login();
        login.setFamilyMember(familyMembers);
        login.setLocalDateTime(LocalDateTime.now());
        login.setCreatedBy(familyMembers.getFamilyGroupId().getFamilyGroupId().toString());
        login.setFamilyPin(familyMembers.getPin());
        String verificationId = UUID.randomUUID().toString();
        login.setVerificationUUID(verificationId);
        loginRepository.save(login);
    }

    public void findFamilyMemberByVerificationId(String verificationId) {
        Optional<FamilyMembers> byVerificationId = familyMemberRepository.findByVerificationUUID(verificationId);
        FamilyMembers familyMembers = byVerificationId.get();
        FamilyGroups familyGroupId = byVerificationId.get().getFamilyGroupId();
        familyMembers.setStatus(Status.ACTIVE);
        familyGroupId.setStatus(Status.ACTIVE);
        familyMemberRepository.save(familyMembers);
    }

    public String loginExistingMember(LoginDTO loginDTO) {
        Optional<FamilyMembers> byMemberLoginId = familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId());
        if (byMemberLoginId.isEmpty()) {
            return "Member Login Id does not exist : " + loginDTO.getMemberLoginId()+"  Signup please.";
        }
        FamilyMembers familyMembers = byMemberLoginId.get();
        if (familyMembers.getMemberLogin().equals(loginDTO.getMemberLoginId())) {
            if (familyMembers.getPin().equals(loginDTO.getPin()) && familyMembers.getStatus().equals(Status.ACTIVE)) {
                String otp = generateOtp();
                smsService.sendSms(familyMembers.getHomePhoneNo(), "Your OTP number is " + otp);
                familyMembers.setOtp(otp);
                familyMemberRepository.save(familyMembers);
                return "OTP sent successfully";
            }
        }
        if (familyMembers.getMemberLogin().equals(loginDTO.getMemberLoginId()) && familyMembers.getStatus().equals(Status.INACTIVE)) {
            if (!familyMembers.getPin().equals(loginDTO.getPin())) {
                throw new InvalidMemberIdException("Password does not match with Member login Id: " + familyMembers.getMemberLogin());
            } else {
                String verificationId = UUID.randomUUID().toString();
                familyMembers.setVerificationUUID(verificationId);
                String verificationLink = "http://localhost:8082/api/v1/familymember/verify/" + verificationId;
                   smsService.verificationLink(familyMembers.getHomePhoneNo(), verificationLink);
                return "Verification link sent successfully";
            }
        }
        if (loginDTO.getMemberLoginId().equals(familyMembers.getMemberLogin()) && loginDTO.getPin().equals(familyMembers.getPin()) && familyMembers.getStatus().equals(Status.ACTIVE)) {
            return "Successfully verified";
        }
        return "Invalid " ;
    }

    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private String setFamilyMemberDetails(MemberRegistrationDTO memberRegistrationDTO, FamilyMembers familyMembers, String pin) {
        familyMembers.setMemberName(memberRegistrationDTO.getMemberName());
        familyMembers.setDob(memberRegistrationDTO.getDob());
        familyMembers.setGender(memberRegistrationDTO.getGender());
        familyMembers.setEmail(memberRegistrationDTO.getEmail());
        familyMembers.setStreetNo(memberRegistrationDTO.getStreetNo());
        familyMembers.setStreetName(memberRegistrationDTO.getStreetName());
        familyMembers.setCity(memberRegistrationDTO.getCity());
        familyMembers.setProvince(memberRegistrationDTO.getProvince());
        familyMembers.setPostalCode(memberRegistrationDTO.getPostalCode());
        familyMembers.setPreferredMode(memberRegistrationDTO.getPreferredMode());
        familyMembers.setMemberLogin(memberRegistrationDTO.getMemberLoginId());
        familyMembers.setPin(pin);
        familyMembers.setCountry(memberRegistrationDTO.getCountry());
        familyMembers.setHomePhoneNo(memberRegistrationDTO.getHomePhoneNo());
        familyMembers.setBussinessPhoneNo(memberRegistrationDTO.getBussinessPhoneNo());
        familyMembers.setLanguage(memberRegistrationDTO.getLanguage());
        familyMembers.setStatus(Status.INACTIVE);
        String verificationId = UUID.randomUUID().toString();
        familyMembers.setVerificationUUID(verificationId);
        String verificationLink = "http://localhost:8082/api/familyregistration/verify/" + verificationId;
        smsService.sendSms(familyMembers.getHomePhoneNo(), "Please verify using this link: " + verificationLink);
        String pin1 = familyMembers.getPin();
        return pin1;
    }

    public FamilyGroups createNewFamilyGroup(String pin , MemberRegistrationDTO memberRegistrationDTO) {

        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyPin(pin);
        familyGroups.setStatus(Status.INACTIVE);
        familyGroups.setCredits(0L);
        familyGroups.setCreatedAt(LocalDateTime.now());
        familyGroups.setUpdatedAt(LocalDateTime.now());
        familyGroups.setCreatedBy(memberRegistrationDTO.getFamilyGroupId());
        familyGroups.setLastUpdatedBy(familyGroups.getFamilyGroupId());
        familyGroupRepository.save(familyGroups);
        return familyGroups;
    }

    public String generatePin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    public String findFamilyMemberByOtpVerification(LoginDTO loginDTO) {
        Optional<FamilyMembers> familyMemberOpt = familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId());

        if (familyMemberOpt.isEmpty()) {
            throw new InvalidMemberIdException("This member does not exist");
        }
        FamilyMembers familyMembers = familyMemberOpt.get();
        if (!familyMembers.getMemberLogin().equals(loginDTO.getMemberLoginId())) {
            throw new InvalidMemberIdException("Member with this id does not exist");
        }
        if (!familyMembers.getOtp().equals(loginDTO.getPin())) {
            throw new RuntimeException("Wrong otp");
        }
        familyMembers.setStatus(Status.ACTIVE);
        familyMemberRepository.save(familyMembers);
        return "OTP verified";
    }
}
