package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.FeeType;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCourseFeeDTO;
import io.reactivestax.activelife.dto.UpdateCourseDTO;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OfferedCoursesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OfferredCourseService offerredCourseService;

    @InjectMocks
    private OfferedCourses offeredCoursesController;
    private OfferedCourseDTO offeredCourseDTO;
    private  UpdateCourseDTO updateCourseDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setStartDate(LocalDate.of(2025, 2, 9));
        offeredCourseDTO.setEndDate(null);
        offeredCourseDTO.setNoOfSeats(5L);
        offeredCourseDTO.setStartTime(LocalDateTime.of(2023, 5, 1, 10, 0));
        offeredCourseDTO.setEndTime(LocalDateTime.of(2023, 5, 1, 12, 0));
        offeredCourseDTO.setIsAllDay(IsAllDay.NO);
        offeredCourseDTO.setRegistrationStartDate(LocalDate.of(2023, 4, 15));
        offeredCourseDTO.setAvailableForEnrollment(AvailableForEnrollment.YES);
        offeredCourseDTO.setCoursesId(10L);
        offeredCourseDTO.setFacilities(3L);
        OfferedCourseFeeDTO offeredCourseFeeDTO = new OfferedCourseFeeDTO();
        offeredCourseFeeDTO.setFeeId(10L);
        offeredCourseFeeDTO.setFeeType(FeeType.RESIDENT);
        offeredCourseFeeDTO.setCourseFee(5000L);
        offeredCourseFeeDTO.setCreatedTimestamp(LocalDate.of(2025, 2, 4));
        offeredCourseFeeDTO.setLastUpdatedTimestamp(LocalDate.of(2025, 2, 4));
        offeredCourseFeeDTO.setCreatedBy(1L);
        offeredCourseFeeDTO.setLastUpdatedBy(1L);

        offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseFeeDTO);


        updateCourseDTO = new UpdateCourseDTO();
        updateCourseDTO.setStartDate(LocalDate.of(2023, 1, 1));
        updateCourseDTO.setEndDate(LocalDate.of(2023, 1, 31));
        updateCourseDTO.setNoOfSeats(34L);
        updateCourseDTO.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        updateCourseDTO.setEndTime(LocalDateTime.of(2023, 1, 1, 12, 0));
        updateCourseDTO.setIsAllDay(IsAllDay.YES);
        updateCourseDTO.setNoOfSeats(5L);
        updateCourseDTO.setRegistrationStartDate(LocalDate.of(2023, 1, 1));
        updateCourseDTO.setAvailableForEnrollment(AvailableForEnrollment.NO);


        mockMvc = MockMvcBuilders.standaloneSetup(offeredCoursesController).build();
    }

    @Test
    public void testAddOfferedCourse() throws Exception {
        // Prepare the JSON payload
        String json = """
            {
              "startDate": "2025-02-09",
              "endDate": "2025-09-19",
              "noOfSeats": 5,
              "startTime": "2023-05-01T10:00:00",
              "endTime": "2023-05-01T12:00:00",
              "isAllDay": "NO",
              "registrationStartDate": "2023-04-15",
              "availableForEnrollment": "YES",
              "coursesId": 6,
              "facilities": 6,
              "offeredCourseFeeDTO": {
                "feeType": "RESIDENT",
                "courseFee": 5000.00
              }
            }
        """;

        doNothing().when(offerredCourseService).addOfferedCourseToDatabase(org.mockito.ArgumentMatchers.any(OfferedCourseDTO.class));

        mockMvc.perform(post("/api/offeredcourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Course saved succesffuly"));

        verify(offerredCourseService, times(1)).addOfferedCourseToDatabase(org.mockito.ArgumentMatchers.any(OfferedCourseDTO.class));
    }

    @Test
    public void testGetOfferedCourseById() throws Exception {
        when(offerredCourseService.getOfferedCoursesById(10L)).thenReturn(offeredCourseDTO);

        mockMvc.perform(get("/api/offeredcourse/10"))
                .andExpect(status().isOk());
        verify(offerredCourseService, times(1)).getOfferedCoursesById(10L);
    }


    @Test
    public void testUpdateCourse() throws Exception {

        mockMvc.perform(patch("/api/offeredcourse/10")
                        .contentType("application/json")
                        .content("{"
                                + "\"startDate\": \"2023-01-01\","
                                + "\"endDate\": \"2023-01-31\","
                                + "\"noOfCourses\": 34,"
                                + "\"startTime\": \"2023-01-01T10:00:00\","
                                + "\"endTime\": \"2023-01-01T12:00:00\","
                                + "\"isAllDay\": \"YES\","
                                + "\"noOfSeats\": 5,"
                                + "\"registrationStartDate\": \"2023-01-01\","
                                + "\"availableForEnrollment\": \"NO\""
                                + "}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Courses updated sucessfully : "));

        verify(offerredCourseService, times(1)).updateOfferedCourseToDatabase(updateCourseDTO, 10L);
    }
}
