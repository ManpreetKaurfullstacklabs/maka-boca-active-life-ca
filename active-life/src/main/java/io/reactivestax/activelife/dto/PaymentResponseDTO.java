package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String transactionId;
    private PaymentStatus status;
    private String message;
    private Double amount;
    private Long familyMemberId;
}
