package io.reactivestax.activelife.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShoppingCartDTO {

    private Long familyMemberId;
    private Long familyCourseRegistrationId;
    private Long offeredCourseId;
    private Long noOfItems;
    private Long price;
}
