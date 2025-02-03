package io.reactivestax.activelife.domain.course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.reactivestax.activelife.Enums.AvailableForEnrollment;
import io.reactivestax.activelife.Enums.IsAllDay;
import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.domain.facility.Facilities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "offered_courses")
public class OfferedCourses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offered_course_id")
    private  Long offeredCourseId;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "course_start_date")
    private LocalDate startDate;

    @Column(name = "course_end_date")
    private LocalDate endDate ;

    @Column(name = "total_of_classes")
    private Long noOfClasses;

    @Column(name="course_start_time")
    private LocalDateTime startTime;

    @Column(name="course_end_time")
    private LocalDateTime endTime;

    @Column(name="is_course_allday")
    @Enumerated(EnumType.STRING)
    private IsAllDay isAllDay;

    @Column(name="registration_start_date")
    private LocalDate registrationStartDate;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="courses_fee_id")
    private OfferedCourseFee offeredCourseFee;

    @Column(name="available_for_enrollment")
    @Enumerated(EnumType.STRING)
    private AvailableForEnrollment availableForEnrollment;

    @Column(name = "created_at")
    private  LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private  Courses  courses;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facilities facilities;

    @Column(name = "last_updated_at")
    private LocalDate lastUpdatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "no_of_seats")
    private Long noOfSeats;

    @Column(name = "cost")
    private Long cost;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @Column(name="isWaitlisted")
    @Enumerated(EnumType.STRING)
    private IsWaitListed isWaitListed;
}
