package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyCourseRegistrationDTO {

    private Long familyCourseRegistrationId;

    private Long cost;

    private LocalDate enrollmentDate;

    private IsWithdrawn isWithdrawn;

    private Long withdrawnCredits;

    private String enrollmentActor ;

    private Long enrollmentActorId;

    private LocalDateTime createdAt;

    private  Long offeredCourseId;

    private Long familyMemberId;

    private LocalDateTime lastUpdatedTime;

    private Long createdBy;

    private Long lastUpdateBy;

}
