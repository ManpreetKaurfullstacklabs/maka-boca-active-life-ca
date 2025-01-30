package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.Login;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.FamilyMemberDTO;
import io.reactivestax.activelife.exception.MemberNotFoundException;
import io.reactivestax.activelife.repository.FamilMemberRepository;
import io.reactivestax.activelife.repository.FamilyGroupRepository;
import io.reactivestax.activelife.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilMemberRepository familyMemberRepository;

    @Autowired
    private FamilyGroupRepository familyGroupRepository;

    @Autowired
    private LoginRepository loginRepository;



    @Transactional
    public void addNewFamilyMemberOnSignup(FamilyMemberDTO familyMemberDTO) {
        Optional<FamilyMembers> existingFamilyMember = familyMemberRepository.findByMemberLogin(familyMemberDTO.getMemberLoginId());

        FamilyMembers familyMembers = existingFamilyMember.orElseGet(FamilyMembers::new);
        if (existingFamilyMember.isEmpty()) {
            FamilyGroups familyGroups = createNewFamilyGroup(familyMemberDTO);
            familyMembers.setFamilyGroup(familyGroups);
        }
        setFamilyMemberDetails(familyMemberDTO, familyMembers);
        familyMemberRepository.save(familyMembers);

        if (existingFamilyMember.isEmpty()) {
            saveLoginAudit(familyMembers);
        }
    }

    public void addFamilyToExistingGroupID(FamilyMemberDTO familyMemberDTO){
        Optional<FamilyGroups> existingFamilyGroup = familyGroupRepository.findById(familyMemberDTO.getFamilyGroups().getFamilyGroupId());

        if(existingFamilyGroup.isPresent()){
            FamilyGroups familyGroups = existingFamilyGroup.get();
            FamilyMembers familyMembers = new FamilyMembers();
            setFamilyMemberDetails(familyMemberDTO,familyMembers);
            familyMembers.setFamilyGroup(familyGroups);
            familyMemberRepository.save(familyMembers);
            saveLoginAudit(familyMembers);
        }

    }

    public FamilyMemberDTO getAllMembersbygivenMemberId(Long  id){

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
            familyMemberDTO.setMemberLoginId(familyMembers.getMemberLogin());
            familyMemberDTO.setGroupOwner(familyMembers.getGroupOwner());
            familyMemberDTO.setCountry(familyMembers.getCountry());
            familyMemberDTO.setHomePhoneNo(familyMembers.getHomePhoneNo());
            familyMemberDTO.setBussinessPhoneNo(familyMembers.getBussinessPhoneNo());
            familyMemberDTO.setLanguage(familyMembers.getLanguage());
            familyMemberDTO.setStatus(familyMembers.getStatus());
            familyMemberDTO.setFamilyGroups(familyMembers.getFamilyGroup());
            return  familyMemberDTO;
        }
        else{
            throw new MemberNotFoundException("this member is not registered ");
        }

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
        familyMembers.setCountry(familyMemberDTO.getCountry());
        familyMembers.setHomePhoneNo(familyMemberDTO.getHomePhoneNo());
        familyMembers.setBussinessPhoneNo(familyMemberDTO.getBussinessPhoneNo());
        familyMembers.setLanguage(familyMemberDTO.getLanguage());
        familyMembers.setMemberLogin(familyMemberDTO.getMemberLoginId());
        familyMembers.setStatus(familyMemberDTO.getStatus());
    }

    public FamilyGroups createNewFamilyGroup(FamilyMemberDTO familyMemberDTO) {
        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyPin("1234"); // Secure pin
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
        login.setFamilyPin(familyMembers.getFamilyGroup().getFamilyPin());
        loginRepository.save(login);
    }
}
