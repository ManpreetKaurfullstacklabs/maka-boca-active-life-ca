package io.reactivestax.activelife.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.activelife.dto.FamilyCourseRegistrationDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(FamilyMemberCourseRegistration.class)
class MemberRegistrationCourseRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    private FamilyCourseRegistrationDTO familyCourseRegistrationDTO;
    private String jsonRequest;

    @BeforeEach
    void setUp() throws Exception {

        familyCourseRegistrationDTO = new FamilyCourseRegistrationDTO();
        familyCourseRegistrationDTO.setFamilyCourseRegistrationId(1L);
        familyCourseRegistrationDTO.setEnrollmentDate(LocalDate.now());
        familyCourseRegistrationDTO.setIsWithdrawn(null);

        familyCourseRegistrationDTO.setCreatedAt(LocalDateTime.now());
        familyCourseRegistrationDTO.setOfferedCourseId(101L);
        familyCourseRegistrationDTO.setFamilyMemberId(1001L);
        familyCourseRegistrationDTO.setLastUpdatedTime(LocalDateTime.now());
        familyCourseRegistrationDTO.setCreatedBy(1L);
        familyCourseRegistrationDTO.setLastUpdateBy(1L);

        jsonRequest = new ObjectMapper().writeValueAsString(familyCourseRegistrationDTO);
    }

//    @Test
//    void testAddNewMemberToOfferedCourse() throws Exception {
//        doNothing().when(familyCourseRegistrationService).enrollFamilyMemberInCourse(any(FamilyCourseRegistrationDTO.class));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/courseregistration/member")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonRequest))
//                .andExpect(MockMvcResultMatchers.content().string("family member added successfully to a course : "));
//    }

    @Test
    void testGetMemberDetailsWithId() throws Exception {
        when(familyCourseRegistrationService.getAllFamilyMemberRegistration(1L)).thenReturn(familyCourseRegistrationDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/courseregistration/member/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.familyCourseRegistrationId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.familyMemberId").value(1001L));
        verify(familyCourseRegistrationService, times(1)).getAllFamilyMemberRegistration(1L);
    }

    @Test
    void testWithdrawMemberFromCourse() throws Exception {
        doNothing().when(familyCourseRegistrationService).deleteFamilyMemberFromRegisteredCourse(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/courseregistration/member/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("family member withdraw from course successfully"));
        verify(familyCourseRegistrationService, times(1)).deleteFamilyMemberFromRegisteredCourse(1L);
    }

    @Test
    void testUpdateMemberInformation() throws Exception {
        when(familyCourseRegistrationService.updateFamilyMemberRegistration(1L, familyCourseRegistrationDTO)).thenReturn(familyCourseRegistrationDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/courseregistration/member/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
