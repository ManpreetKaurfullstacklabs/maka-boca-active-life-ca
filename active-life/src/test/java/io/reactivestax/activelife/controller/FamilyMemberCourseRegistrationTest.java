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
class FamilyMemberCourseRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    private FamilyCourseRegistrationDTO familyCourseRegistrationDTO;


    @BeforeEach
    void setUp() {
        familyCourseRegistrationDTO = new FamilyCourseRegistrationDTO();
        familyCourseRegistrationDTO.setFamilyCourseRegistrationId(1L);
        familyCourseRegistrationDTO.setEnrollmentDate(LocalDate.now());
        familyCourseRegistrationDTO.setIsWithdrawn(null);  // Set according to your business logic
        familyCourseRegistrationDTO.setWithdrawnCredits(0L);  // Set default or test value
        familyCourseRegistrationDTO.setCreatedAt(LocalDateTime.now());
        familyCourseRegistrationDTO.setOfferedCourseId(101L);
        familyCourseRegistrationDTO.setFamilyMemberId(1001L);
        familyCourseRegistrationDTO.setLastUpdatedTime(LocalDateTime.now());
        familyCourseRegistrationDTO.setCreatedBy(1L);
        familyCourseRegistrationDTO.setLastUpdateBy(1L);
    }


    @Test
    void testAddNewMemberToOfferedCourse() throws Exception {
        doNothing().when(familyCourseRegistrationService).enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
        String jsonRequest = new ObjectMapper().writeValueAsString(familyCourseRegistrationDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/courseregistration/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("family member added sucessfully to a course : "));
        verify(familyCourseRegistrationService, times(1)).enrollFamilyMemberInCourse(familyCourseRegistrationDTO);
    }

    @Test
    void getMemberDetailsWithId() {
    }

    @Test
    void updateMemberInformation() {
    }

    @Test
    void withdrawMemberFromCourse() {
    }
}
