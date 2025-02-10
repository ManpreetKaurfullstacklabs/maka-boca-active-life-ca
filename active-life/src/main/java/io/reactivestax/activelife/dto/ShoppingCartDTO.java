package io.reactivestax.activelife.dto;

import lombok.Data;

@Data
public class ShoppingCartDTO {

    private Long familyMemberId;
    private Long offeredCourseId;
    private Long noOfItems;
    private Long price;

}
