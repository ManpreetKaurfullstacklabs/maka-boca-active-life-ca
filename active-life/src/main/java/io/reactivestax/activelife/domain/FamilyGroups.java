package io.reactivestax.activelife.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
// for audit purpose only not interactiong with user
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyGroups {
    private String familyPin;
    private BigInteger credits;
    private String status;
    private int failedAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigInteger createdBy;
    private BigInteger lastUpdatedBy;
}
