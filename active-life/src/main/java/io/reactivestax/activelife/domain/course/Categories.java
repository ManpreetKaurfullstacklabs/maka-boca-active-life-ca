package io.reactivestax.activelife.domain.course;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "category")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDate createdTimestamp;

    @Column(name = "last_updated_at")
    private LocalDate lastUpdatedTimestamp;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "lastUpdated_by")
    private Long lastUpdatedBy;

}
