package io.reactivestax.activelife.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.activelife.Enums.GroupOwner;
import io.reactivestax.activelife.Enums.PreferredMode;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.FamilyMemberDTO;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.interfaces.FamilyMemberMapper;
import io.reactivestax.activelife.repository.familymember.FamilMemberRepository;
import io.reactivestax.activelife.repository.familymember.FamilyGroupRepository;
import io.reactivestax.activelife.service.FamilyMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(FamilyMember.class)
class FamilyMemberTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FamilyMemberService familyMemberService;

    @Mock
    private FamilMemberRepository familyMemberRepository;

    @Mock
    private FamilyMemberMapper familyMemberMapper;

    @Mock
    private FamilyGroupRepository familyGroupRepository;

    private FamilyMemberDTO testFamilyMemberDTO;
    private FamilyMembers testFamilyMember;



    @BeforeEach
    void setUp() {

        String pin = "790059";
        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyPin(pin);
        familyGroups.setStatus(Status.INACTIVE);
        familyGroups.setCredits(0L);
        familyGroups.setCreatedAt(LocalDateTime.now());
        familyGroups.setUpdatedAt(LocalDateTime.now());
        familyGroups.setCreatedBy(1L);
        familyGroups.setLastUpdatedBy(1L);

        when(familyGroupRepository.save(any(FamilyGroups.class))).thenReturn(familyGroups);

        testFamilyMember = new FamilyMembers();
        testFamilyMember.setMemberName("akshi");
        testFamilyMember.setDob(LocalDate.of(1995, 3, 30));
        testFamilyMember.setGender("MALE");
        testFamilyMember.setEmail("sukh@example.com");
        testFamilyMember.setStreetNo("789");
        testFamilyMember.setStreetName("Oak Avenue");
        testFamilyMember.setPreferredMode(PreferredMode.SMS);
        testFamilyMember.setCity("Toronto");
        testFamilyMember.setProvince("ON");
        testFamilyMember.setCountry("usa");
        testFamilyMember.setPostalCode("V6B 3K9");
        testFamilyMember.setHomePhoneNo("+13657781555");
        testFamilyMember.setBussinessPhoneNo("604-876-5432");
        testFamilyMember.setGroupOwner(GroupOwner.YES);
        testFamilyMember.setStatus(Status.ACTIVE);
        testFamilyMember.setMemberLogin("4");

        testFamilyMemberDTO = new FamilyMemberDTO();
        testFamilyMemberDTO.setMemberLoginId("4");
        testFamilyMemberDTO.setMemberName("akshi");
        testFamilyMemberDTO.setDob(LocalDate.of(1995, 3, 30));
        testFamilyMemberDTO.setGender("MALE");
        testFamilyMemberDTO.setEmail("sukh@example.com");
        testFamilyMemberDTO.setStreetNo("789");
        testFamilyMemberDTO.setStreetName("Oak Avenue");
        testFamilyMemberDTO.setPreferredMode(PreferredMode.SMS);
        testFamilyMemberDTO.setCity("Toronto");
        testFamilyMemberDTO.setProvince("ON");
        testFamilyMemberDTO.setCountry("usa");
        testFamilyMemberDTO.setPostalCode("V6B 3K9");
        testFamilyMemberDTO.setHomePhoneNo("+13657781555");
        testFamilyMemberDTO.setBussinessPhoneNo("604-876-5432");
        testFamilyMemberDTO.setLanguage("English");
        testFamilyMemberDTO.setGroupOwner(GroupOwner.YES);
        testFamilyMemberDTO.setStatus(Status.ACTIVE);
        testFamilyMemberDTO.setFamilyGroupId(2L);
    }


    @Test
    void testAddFamilyMemberWithOKResonse() throws Exception {
        FamilyMemberDTO familyMemberDTO = new FamilyMemberDTO();
        familyMemberDTO.setMemberName("akshi");
        familyMemberDTO.setDob(LocalDate.of(1995, 3, 30));
        familyMemberDTO.setGender("MALE");
        familyMemberDTO.setEmail("sukh@example.com");
        familyMemberDTO.setStreetNo("789");
        familyMemberDTO.setStreetName("Oak Avenue");
        familyMemberDTO.setPreferredMode(PreferredMode.SMS);
        familyMemberDTO.setCity("Toronto");
        familyMemberDTO.setProvince("ON");
        familyMemberDTO.setCountry("usa");
        familyMemberDTO.setPostalCode("V6B 3K9");
        familyMemberDTO.setHomePhoneNo("+13657781555");
        familyMemberDTO.setBussinessPhoneNo("604-876-5432");
        familyMemberDTO.setLanguage("English");
        familyMemberDTO.setMemberLoginId("4");
        familyMemberDTO.setGroupOwner(GroupOwner.YES);
        familyMemberDTO.setStatus(Status.ACTIVE);
        familyMemberDTO.setFamilyGroupId(2L);

        String generatedPin = "790059";
        when(familyMemberService.addNewFamilyMemberOnSignup(familyMemberDTO)).thenReturn(generatedPin);

        String jsonRequest = new ObjectMapper().writeValueAsString(familyMemberDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/familymember/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("family member added successfully with pin number : " + generatedPin));
    }



    @Test
    void testGetAllMembersbygivenMemberId_Success() throws Exception {
        String memberId = "4";
        when(familyMemberRepository.findByMemberLogin(memberId)).thenReturn(Optional.of(testFamilyMember));
        when(familyMemberMapper.toDto(testFamilyMember)).thenReturn(testFamilyMemberDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/familymember/member/{id}", memberId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    void updateMemberInformation() throws Exception {
        FamilyMemberDTO familyMemberDTO = new FamilyMemberDTO();
        familyMemberDTO.setMemberLoginId("4");
        familyMemberDTO.setMemberName("akshi_updated");

        String jsonRequest = new ObjectMapper().writeValueAsString(familyMemberDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/familymember/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(familyMemberDTO)));
    }

    @Test
    void deactivateMember() throws Exception {
        Long memberId = 4L;
        doNothing().when(familyMemberService).deleteFamilyMemberById(memberId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/familymember/members/{id}", memberId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("family member removed successfully"));
    }

    @Test
    void verifySignup() throws Exception {
        String verificationId = "verify123";
        doNothing().when(familyMemberService).findFamilyMemberByVerificationId(verificationId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/familymember/verify/{id}", verificationId))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("verified"));
    }

    @Test
    void verifyLogin() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("4");
        loginDTO.setPin("password123");



        String jsonRequest = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/familymember/login/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("verified"));
    }

    @Test
    void postLoginWithOkResponse() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMemberLoginId("4");
        loginDTO.setPin("password123");

        String jsonRequest = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/familymember/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }



}
