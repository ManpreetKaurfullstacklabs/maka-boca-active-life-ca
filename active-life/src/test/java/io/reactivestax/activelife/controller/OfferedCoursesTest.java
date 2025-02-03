package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.FeeType;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OfferedCourses.class)
class OfferedCoursesTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private OfferredCourseService offerredCourseService;

    OfferedCourseDTO offeredCourseDTO = new OfferedCourseDTO();

    @BeforeEach
    void setUp() {

        OfferedCourseFee offeredCourseFee = new OfferedCourseFee();
        offeredCourseFee.setFeeId(1L);
        offeredCourseFee.setFeeType(FeeType.RESIDENT);
        offeredCourseFee.setCourseFee(20L);


        offeredCourseDTO.setBarcode("COURSE123");
        offeredCourseDTO.setStartDate(LocalDate.of(2025, 2, 2));
        offeredCourseDTO.setEndDate(LocalDate.of(2025, 2, 5));
        offeredCourseDTO.setNoOfSeats(50L);
        offeredCourseDTO.setStartTime(LocalDateTime.of(2025, 2, 2, 9, 0));
        offeredCourseDTO.setEndTime(LocalDateTime.of(2025, 2, 2, 17, 0));
        offeredCourseDTO.setIsAllDay(IsAllDay.NO);
        offeredCourseDTO.setRegistrationStartDate(LocalDate.of(2025, 1, 1));
        offeredCourseDTO.setAvailableForEnrollment(AvailableForEnrollment.YES);
        offeredCourseDTO.setUpdatedAt(LocalDateTime.of(2025, 1, 15, 12, 0));
        offeredCourseDTO.setCoursesId(1L);
        offeredCourseDTO.setFacilities(1L);
        offeredCourseDTO.setOfferedCourseFee(offeredCourseFee);
    }


    @Test
    void addNewCourseToOfferedCourse() throws Exception {
        doNothing().when(offerredCourseService).addOfferedCourseToDatabase(any(OfferedCourseDTO.class));

        String courseJson = "{\n" +
                "  \"barcode\": \"COURSE123\",\n" +
                "  \"startDate\": \"2025-02-02\",\n" +
                "  \"endDate\": \"2025-02-05\",\n" +
                "  \"noOfSeats\": 50,\n" +
                "  \"startTime\": \"2025-02-02T09:00:00\",\n" +
                "  \"endTime\": \"2025-02-02T17:00:00\",\n" +
                "  \"isAllDay\": \"NO\",\n" +
                "  \"registrationStartDate\": \"2025-01-01\",\n" +
                "  \"availableForEnrollment\": \"YES\",\n" +
                "  \"updatedAt\": \"2025-01-15T12:00:00\",\n" +
                "  \"coursesId\": 1,\n" +
                "  \"facilities\": 1,\n" +
                "  \"offeredCourseFee\": {\n" +
                "    \"feeId\": 1,\n" +
                "    \"feeType\": \"RESIDENT\",\n" +
                "    \"courseFee\": 20\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/api/v1/offeredcourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Courses added sucessfully : "));
    }

    @Test
    void getOfferedCourse() throws Exception {

        when(offerredCourseService.getOfferedCoursesById(1L)).thenReturn(offeredCourseDTO);

        mockMvc.perform(get("/api/v1/offeredcourse/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expect status OK (200)
                .andExpect(content().json("{\n" +
                        "  \"barcode\": \"COURSE123\",\n" +
                        "  \"startDate\": \"2025-02-02\",\n" +
                        "  \"endDate\": \"2025-02-05\",\n" +
                        "  \"noOfSeats\": 50,\n" +
                        "  \"startTime\": \"2025-02-02T09:00:00\",\n" +
                        "  \"endTime\": \"2025-02-02T17:00:00\",\n" +
                        "  \"isAllDay\": \"NO\",\n" +
                        "  \"registrationStartDate\": \"2025-01-01\",\n" +
                        "  \"availableForEnrollment\": \"YES\",\n" +
                        "  \"updatedAt\": \"2025-01-15T12:00:00\",\n" +
                        "  \"coursesId\": 1,\n" +
                        "  \"facilities\": 1\n" +
                        "}"));
    }
    @Test
    void updateCourseToOfferedCourse() throws Exception {

        doNothing().when(offerredCourseService).updateOfferedCourseToDatabase(any(OfferedCourseDTO.class), any(Long.class));

        String updatedCourseJson = "{\n" +
                "  \"barcode\": \"COURSE123\",\n" +
                "  \"startDate\": \"2025-02-02\",\n" +
                "  \"endDate\": \"2025-02-05\",\n" +
                "  \"noOfSeats\": 60,\n" +
                "  \"startTime\": \"2025-02-02T09:00:00\",\n" +
                "  \"endTime\": \"2025-02-02T17:00:00\",\n" +
                "  \"isAllDay\": \"NO\",\n" +
                "  \"registrationStartDate\": \"2025-01-01\",\n" +
                "  \"availableForEnrollment\": \"YES\",\n" +
                "  \"updatedAt\": \"2025-01-15T12:00:00\",\n" +
                "  \"coursesId\": 1,\n" +
                "  \"facilities\": 1\n" +
                "}";

        mockMvc.perform(patch("/api/v1/offeredcourse/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCourseJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Courses updated sucessfully : "));
}}
