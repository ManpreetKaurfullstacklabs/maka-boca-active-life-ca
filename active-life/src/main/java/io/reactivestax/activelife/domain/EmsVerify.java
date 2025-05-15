package io.reactivestax.activelife.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmsVerify {
    private String customerId;
    private String otp;
}
