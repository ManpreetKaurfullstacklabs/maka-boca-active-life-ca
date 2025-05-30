package io.reactivestax.activelife.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class FamilyCourseRegistrationDTO {

    private Long familyCourseRegistrationId;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate enrollmentDate;

    private IsWithdrawn isWithdrawn;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private  Long offeredCourseId;

    private Long familyMemberId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdatedTime;

    private Long createdBy;

    private Long lastUpdateBy;

    private OfferedCourseDTO offeredCourseDTO;

}
