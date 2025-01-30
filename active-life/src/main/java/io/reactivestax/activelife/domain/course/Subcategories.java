package io.reactivestax.activelife.domain.course;

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
@Entity(name = "sub_categories")

public class Subcategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategory_id")
    private Long subCategoryId;

    @Column(name = "category_name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "categories")
    private Categories categories;

    @Column(name = "created_at")
    private LocalDate createdTimestamp;

    @Column(name = "last_updated_at")
    private LocalDate lastUpdated ;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "last_updated_by")
    private  Long lastUpdatedBy;

}
