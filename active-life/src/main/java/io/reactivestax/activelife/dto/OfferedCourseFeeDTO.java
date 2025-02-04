package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.FeeType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferedCourseFeeDTO {

    @Null(message = "Fee ID should be null for new entries")
    private Long feeId;

    @NotNull(message = "Fee Type cannot be null")
    private FeeType feeType;

    @NotNull(message = "Course Fee cannot be null")
    @Positive(message = "Course Fee must be a positive value")
    private Long courseFee;

    @PastOrPresent(message = "Created timestamp cannot be in the future")
    private LocalDate createdTimestamp;

    @PastOrPresent(message = "Last updated timestamp cannot be in the future")
    private LocalDate lastUpdatedTimestamp;

    @NotNull(message = "Created By cannot be null")
    @Positive(message = "Created By must be a positive value")
    private Long createdBy;

    @NotNull(message = "Last Updated By cannot be null")
    @Positive(message = "Last Updated By must be a positive value")
    private Long lastUpdatedBy;
}
