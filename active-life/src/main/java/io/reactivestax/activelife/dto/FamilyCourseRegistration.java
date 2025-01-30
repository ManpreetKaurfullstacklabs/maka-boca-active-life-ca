package io.reactivestax.activelife.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class FamilyCourseRegistration {

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
