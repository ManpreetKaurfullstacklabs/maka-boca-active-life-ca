package io.reactivestax.activelife.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyCourseRegistrations {


    private BigInteger familyCourseRegistrationId;

    private BigDecimal cost;

    private Date enrollmentDate;

    private Character isWithdrawn;

    private BigDecimal withdrawnCredits;

    private String enrollmentActor ;

    private BigInteger enrollmentActorId;

    private LocalDateTime createdAt;

    private  BigDecimal offeredCourseId;

    private BigDecimal familyMemberId;

    private LocalDateTime lastUpdatedTime;

    private BigDecimal createdBy;

    private BigDecimal lastUpdateBy;

}
