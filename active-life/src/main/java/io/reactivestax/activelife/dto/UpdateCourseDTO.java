package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsAllDay;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateCourseDTO {


    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Number of seats must be at least 1")
    private Long noOfSeats;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "All day flag is required")
    private IsAllDay isAllDay;

    @NotNull(message = "Registration start date is required")
    private LocalDate registrationStartDate;

    @NotNull(message = "Available for enrollment status is required")
    private AvailableForEnrollment availableForEnrollment;
}
