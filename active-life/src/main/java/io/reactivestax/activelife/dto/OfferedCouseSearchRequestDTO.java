package io.reactivestax.activelife.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferedCouseSearchRequestDTO {
    String courseName;
    LocalDate startDate;
    LocalDate endDate;
    String city;
    String province;
    String categoryName;
    String subCategory;
    int ageGroup;

}
