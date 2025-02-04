package io.reactivestax.activelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferedCouseSearchRequestDTO {

    @NotEmpty(message = "Course name is required")
    private String courseName;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDate endDate;

    @NotEmpty(message = "City is required")
    private String city;

    @NotEmpty(message = "Province is required")
    private String province;

    @NotEmpty(message = "Category name is required")
    private String categoryName;

    @NotEmpty(message = "Subcategory is required")
    private String subCategory;

    @Min(value = 1, message = "Age group must be at least 1")
    @Max(value = 120, message = "Age group cannot be greater than 120")
    private int ageGroup;

}
