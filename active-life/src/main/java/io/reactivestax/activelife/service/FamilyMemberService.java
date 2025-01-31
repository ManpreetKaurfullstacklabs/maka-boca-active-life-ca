package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.distribution.SmsService;
import io.reactivestax.activelife.distribution.MessageProducer;
import io.reactivestax.activelife.domain.Login;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.FamilyMemberDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.exception.MemberNotFoundException;
import io.reactivestax.activelife.repository.FamilMemberRepository;
import io.reactivestax.activelife.repository.FamilyGroupRepository;
import io.reactivestax.activelife.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilMemberRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private SmsService smsService;



    @Transactional
    public void addNewFamilyMemberOnSignup(FamilyMemberDTO familyMemberDTO) {

        Optional<FamilyMembers> existingFamilyMember = familyMemberRepository.findByMemberLogin(familyMemberDTO.getMemberLoginId());
        FamilyMembers familyMembers = existingFamilyMember.orElseGet(FamilyMembers::new);
        if (familyMemberDTO.getFamilyGroupId() != null) {
            addFamilyToExistingGroupID(familyMemberDTO, familyMembers);
        } else {
            FamilyGroups familyGroups = createNewFamilyGroup();
            familyMembers.setFamilyGroupId(familyGroups);
            familyGroupRepository.save(familyGroups);
        }
        setFamilyMemberDetails(familyMemberDTO, familyMembers);
        familyMemberRepository.save(familyMembers);
        if (existingFamilyMember.isEmpty()) {
            saveLoginAudit(familyMembers);
        }
    }

    public void addFamilyToExistingGroupID(FamilyMemberDTO familyMemberDTO, FamilyMembers familyMembers) {
        Optional<FamilyGroups> existingFamilyGroup = familyGroupRepository.findById(familyMemberDTO.getFamilyGroupId());
        if (existingFamilyGroup.isPresent()) {
            FamilyGroups familyGroups = existingFamilyGroup.get();
            familyMembers.setFamilyGroupId(familyGroups);
            familyMemberRepository.save(familyMembers);
            saveLoginAudit(familyMembers);
        } else {
            setFamilyMemberDetails(familyMemberDTO,familyMembers);
        }
    }


    public FamilyMemberDTO getAllMembersbygivenMemberId(String  id){
        Optional<FamilyMembers> byMemberLogin = familyMemberRepository.findByMemberLogin(id);
        FamilyMembers familyMembers = byMemberLogin.get();
        if(byMemberLogin.isPresent()){
            FamilyMemberDTO familyMemberDTO = new FamilyMemberDTO();
            familyMemberDTO.setMemberName(familyMembers.getMemberName());
            familyMemberDTO.setDob(familyMembers.getDob());
            familyMemberDTO.setGender(familyMembers.getGender());
            familyMemberDTO.setEmail(familyMembers.getEmail());
            familyMemberDTO.setStreetNo(familyMembers.getStreetNo());
            familyMemberDTO.setStreetName(familyMembers.getStreetName());
            familyMemberDTO.setCity(familyMembers.getCity());
            familyMemberDTO.setProvince(familyMembers.getProvince());
            familyMemberDTO.setPostalCode(familyMembers.getPostalCode());
            familyMemberDTO.setPreferredMode(familyMembers.getPreferredMode());
            familyMemberDTO.setPin(familyMembers.getPin());
            familyMemberDTO.setMemberLoginId(familyMembers.getMemberLogin());
            familyMemberDTO.setGroupOwner(familyMembers.getGroupOwner());
            familyMemberDTO.setCountry(familyMembers.getCountry());
            familyMemberDTO.setHomePhoneNo(familyMembers.getHomePhoneNo());
            familyMemberDTO.setBussinessPhoneNo(familyMembers.getBussinessPhoneNo());
            familyMemberDTO.setLanguage(familyMembers.getLanguage());
            familyMemberDTO.setStatus(familyMembers.getStatus());
            familyMemberDTO.setFamilyGroupId(familyMembers.getFamilyGroupId().getFamilyGroupId());
            return  familyMemberDTO;
        }
        else{
            throw new MemberNotFoundException("this member is not registered ");
        }

    }

    public void updateExistingFamilyMember(FamilyMemberDTO  familyMemberDTO){
        Optional<FamilyMembers> byId = familyMemberRepository.findByMemberLogin(familyMemberDTO.getMemberLoginId());
        FamilyMembers familyMembers = byId.get();
        familyMembers.setMemberName(familyMemberDTO.getMemberName());
        familyMembers.setMemberName(familyMemberDTO.getMemberName());
        familyMembers.setDob(familyMemberDTO.getDob());
        familyMembers.setGender(familyMemberDTO.getGender());
        familyMembers.setEmail(familyMemberDTO.getEmail());
        familyMembers.setStreetNo(familyMemberDTO.getStreetNo());
        familyMembers.setStreetName(familyMemberDTO.getStreetName());
        familyMembers.setCity(familyMemberDTO.getCity());
        familyMembers.setProvince(familyMemberDTO.getProvince());
        familyMembers.setPostalCode(familyMemberDTO.getPostalCode());
        familyMembers.setPin(familyMemberDTO.getPin());
        familyMembers.setPreferredMode(familyMemberDTO.getPreferredMode());
        familyMembers.setGroupOwner(familyMemberDTO.getGroupOwner());
        familyMembers.setCountry(familyMemberDTO.getCountry());
        familyMembers.setHomePhoneNo(familyMemberDTO.getHomePhoneNo());
        familyMembers.setBussinessPhoneNo(familyMemberDTO.getBussinessPhoneNo());
        familyMembers.setLanguage(familyMemberDTO.getLanguage());
        familyMemberRepository.save(familyMembers);

    }
    public void deleteFamilyMemberById(long id ){// not a hard delete its soft delete
        FamilyMembers familyMembers = familyMemberRepository.findById(id).get();
        familyMembers.setStatus(Status.INACTIVE);
        familyMemberRepository.save(familyMembers);

    }
    private void setFamilyMemberDetails(FamilyMemberDTO familyMemberDTO, FamilyMembers familyMembers) {

        familyMembers.setMemberName(familyMemberDTO.getMemberName());
        familyMembers.setDob(familyMemberDTO.getDob());
        familyMembers.setGender(familyMemberDTO.getGender());
        familyMembers.setEmail(familyMemberDTO.getEmail());
        familyMembers.setStreetNo(familyMemberDTO.getStreetNo());
        familyMembers.setStreetName(familyMemberDTO.getStreetName());
        familyMembers.setCity(familyMemberDTO.getCity());
        familyMembers.setProvince(familyMemberDTO.getProvince());
        familyMembers.setPostalCode(familyMemberDTO.getPostalCode());
        familyMembers.setPreferredMode(familyMemberDTO.getPreferredMode());
        familyMembers.setMemberLogin(familyMemberDTO.getMemberLoginId());
        familyMembers.setGroupOwner(familyMemberDTO.getGroupOwner());
        familyMembers.setPin(familyMemberDTO.getPin());
        familyMembers.setCountry(familyMemberDTO.getCountry());
        familyMembers.setHomePhoneNo(familyMemberDTO.getHomePhoneNo());
        familyMembers.setBussinessPhoneNo(familyMemberDTO.getBussinessPhoneNo());
        familyMembers.setLanguage(familyMemberDTO.getLanguage());
        familyMembers.setStatus(familyMemberDTO.getStatus());
        String verificationId = UUID.randomUUID().toString();
        familyMembers.setVerificationUUID(verificationId);
        String verificationLink = "http://localhost:8082/api/v1/familymember//verify"+ verificationId;
      // smsService.sendSms(verificationId, verificationLink);
    }
    public FamilyGroups createNewFamilyGroup() {
        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyPin("1234");
        familyGroups.setStatus(Status.INACTIVE);
        familyGroups.setCredits(new BigDecimal(0));
        familyGroups.setCreatedAt(LocalDateTime.now());
        familyGroups.setUpdatedAt(LocalDateTime.now());
        familyGroups.setCreatedBy(1L);
        familyGroups.setLastUpdatedBy(1L);
        familyGroupRepository.save(familyGroups);
        return familyGroups;
    }

    private void saveLoginAudit(FamilyMembers familyMembers) {
        Login login = new Login();
        login.setFamilyMember(familyMembers);
        login.setLocalDateTime(LocalDateTime.now());
        login.setCreatedBy("System");
        login.setFamilyPin(familyMembers.getPin());
        loginRepository.save(login);
    }

    public void findFamilyMemberByVerificationId(String verificationId){
        Optional<FamilyMembers> byVerificationId = familyMemberRepository.findByVerificationUUID(verificationId);
        FamilyMembers familyMembers = byVerificationId.get();
        familyMembers.setStatus(Status.ACTIVE);
        familyMemberRepository.save(familyMembers);

    }

    public String loginExistingMember(LoginDTO loginDTO) {
        Optional<FamilyMembers> byMemberLoginId = familyMemberRepository.findByMemberLogin(loginDTO.getMemberLoginId());
        if(byMemberLoginId.isEmpty()){
            return "Member Login Id does not exists : "+ loginDTO.getMemberLoginId();
        }
        FamilyMembers familyMembers = byMemberLoginId.get();
        if(familyMembers.getMemberLogin().equals(loginDTO.getMemberLoginId())){
            if(familyMembers.getPin().equals(loginDTO.getPin()) && familyMembers.getStatus().equals(Status.ACTIVE)){
                String otp = generateOtp();
                smsService.sendSms(familyMembers.getHomePhoneNo(),"your otp number is " + otp);
                return "your otp send successfully";
            }
        }

        if(familyMembers.getMemberLogin().equals(loginDTO.getMemberLoginId()) && familyMembers.getStatus().equals(Status.INACTIVE)){
            if(!familyMembers.getPin().equals(loginDTO.getPin())){
                throw  new MemberNotFoundException("password does not match with Member login Id : " + familyMembers.getMemberLogin());
            }
            else {
                String verificationId = UUID.randomUUID().toString();
                familyMembers.setVerificationUUID(verificationId);
                String verificationLink = "http://localhost:8082/api/v1/familymember//verify"+ verificationId+ "verify the link";
                smsService.verificationLink(verificationId, verificationLink);
                return "verification link send successfully";
            }

        }
        if(loginDTO.getMemberLoginId().equals(familyMembers.getMemberLogin()) && loginDTO.getPin().equals(familyMembers.getPin()) && familyMembers.getStatus().equals(Status.ACTIVE)){
            return "successfully verified";
        }
        return "";
    }
    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }


}
