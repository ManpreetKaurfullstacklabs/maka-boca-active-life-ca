package io.reactivestax.activelife.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long familyMemberId;
    private Double amount;
    private String paymentMethod;
    private  Long offeredCourseId;
}
