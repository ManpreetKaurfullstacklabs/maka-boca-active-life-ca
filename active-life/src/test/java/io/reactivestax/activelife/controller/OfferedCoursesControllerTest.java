package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.FeeType;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCourseFeeDTO;
import io.reactivestax.activelife.dto.UpdateCourseDTO;
import io.reactivestax.activelife.service.OfferredCourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    private UpdateCourseDTO updateCourseDTO;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        offeredCourseDTO = new OfferedCourseDTO();
        offeredCourseDTO.setStartDate(LocalDate.of(2025, 2, 4));
        offeredCourseDTO.setEndDate(LocalDate.of(2025, 2, 9));
        offeredCourseDTO.setNoOfSeats(20L);
        offeredCourseDTO.setStartTime(LocalDateTime.of(2025, 2, 4, 9, 0));
        offeredCourseDTO.setEndTime(LocalDateTime.of(2025, 2, 4, 17, 0));
        offeredCourseDTO.setIsAllDay(IsAllDay.YES);
        offeredCourseDTO.setRegistrationStartDate(LocalDate.of(2025, 1, 1));
        offeredCourseDTO.setAvailableForEnrollment(AvailableForEnrollment.YES);
        offeredCourseDTO.setCoursesId(1L);
        offeredCourseDTO.setFacilities(1L);
        updateCourseDTO = new UpdateCourseDTO();
        updateCourseDTO.setStartDate(LocalDate.of(2025, 2, 4));
        updateCourseDTO.setEndDate(LocalDate.of(2025, 2, 9));
        updateCourseDTO.setNoOfSeats(20L);
        updateCourseDTO.setStartTime(LocalDateTime.of(2025, 2, 4, 9, 0));
       updateCourseDTO.setEndTime(LocalDateTime.of(2025, 2, 4, 17, 0));
       updateCourseDTO.setIsAllDay(IsAllDay.YES);
        updateCourseDTO.setRegistrationStartDate(LocalDate.of(2025, 1, 1));
        updateCourseDTO.setAvailableForEnrollment(AvailableForEnrollment.YES);



        OfferedCourseFeeDTO offeredCourseFee = new OfferedCourseFeeDTO();
        offeredCourseFee.setFeeType(FeeType.RESIDENT);
        offeredCourseFee.setCourseFee(200L);

        offeredCourseDTO.setOfferedCourseFeeDTO(offeredCourseFee);

        mockMvc = MockMvcBuilders.standaloneSetup(offeredCoursesController).build();
    }

    @Test
     void testAddOfferedCourse() throws Exception {
        String json ="""
                {
                  "startDate": "2025-02-04",
                  "endDate": "2025-02-09",
                  "noOfSeats": 20,
                  "startTime": "2025-02-04T09:00:00",
                  "endTime": "2025-02-04T17:00:00",
                  "isAllDay": YES,
                  "registrationStartDate": "2025-01-01",
                  "availableForEnrollment": "YES",
                  "coursesId": 1,
                  "facilities": 1,
                  "offeredCourseFee": {
                    "feeType": "RESIDENT",
                    "courseFee": 200
                  }
                }
                
                """;

        doNothing().when(offerredCourseService).addOfferedCourseToDatabase(offeredCourseDTO);

        mockMvc.perform(post("/api/offeredcourse")
                        .contentType("application/json")
                       .content(json))
                .andExpect(status().isOk()) ;
        verify(offerredCourseService, times(1)).addOfferedCourseToDatabase(offeredCourseDTO);
    }



    @Test
    public void testGetOfferedCourseById() throws Exception {
        // Arrange
        when(offerredCourseService.getOfferedCoursesById(1L)).thenReturn(offeredCourseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/offeredcourse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2025-02-04"))
                .andExpect(jsonPath("$.endDate").value("2025-02-09"))
                .andExpect(jsonPath("$.noOfSeats").value(20));

        verify(offerredCourseService, times(1)).getOfferedCoursesById(1L);
    }

    @Test
    public void testUpdateOfferedCourse() throws Exception {
        // Arrange
        doNothing().when(offerredCourseService).updateOfferedCourseToDatabase(updateCourseDTO, 1L);

        // Act & Assert
        mockMvc.perform(patch("/api/offeredcourse/1")
                        .contentType("application/json")
                        .content("{\"startDate\":\"2025-02-04\", \"endDate\":\"2025-02-09\", \"noOfSeats\":20, \"startTime\":\"2025-02-04T09:00:00\", \"endTime\":\"2025-02-04T17:00:00\", \"isAllDay\":false, \"registrationStartDate\":\"2025-01-01\", \"availableForEnrollment\":\"YES\", \"coursesId\":101, \"facilities\":202, \"offeredCourseFee\":{\"feeType\":\"Fixed\",\"courseFee\":200.0}}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Courses updated sucessfully : "));

        verify(offerredCourseService, times(1)).updateOfferedCourseToDatabase(updateCourseDTO, 1L);
    }
}
