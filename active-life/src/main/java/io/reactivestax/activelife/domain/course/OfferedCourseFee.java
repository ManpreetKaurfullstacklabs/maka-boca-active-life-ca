package io.reactivestax.activelife.domain.course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.activelife.Enums.FeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;

@Data
@Entity
@Table(name = "offered_course_fee")
@AllArgsConstructor
@NoArgsConstructor
public class OfferedCourseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offered_course_fee")
    private Long feeId;

    @Column(name = "fee_type")
    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Column(name = "course_fee")
    private BigDecimal courseFee;

    @JsonBackReference
    @OneToMany(mappedBy = "offeredCourseFee", cascade = CascadeType.PERSIST)
    private List<OfferedCourses> offeredCourses;

    @Column(name = "created_at")
    private LocalDate createdTimestamp;

    @Column(name = "lastUpdated_at")
    private LocalDate lastUpdatedTimestamp;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "lastUpdated_by")
    private Long lastUpdatedBy;


}
