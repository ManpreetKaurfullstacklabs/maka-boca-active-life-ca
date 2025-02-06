package io.reactivestax.activelife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferedCouseSearchRequestDTO {


    private String courseName;
    @PastOrPresent(message = "Start date must be in the past or present")
    private LocalDate startDate;

    @FutureOrPresent(message = "End date must be in the future or present")
    private LocalDate endDate;
    private String city;

    private String province;
    private String categoryName;
    private String subCategory;


}
