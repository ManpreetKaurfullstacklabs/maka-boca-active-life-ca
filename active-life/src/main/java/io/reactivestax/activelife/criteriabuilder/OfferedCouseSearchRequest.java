package io.reactivestax.activelife.criteriabuilder;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferedCouseSearchRequest {
    String courseName;
    LocalDate startDate;
    LocalDate endDate;
    String city;
    String province;
    String categoryName;
    String subCategory;
    int ageGroup;

}
