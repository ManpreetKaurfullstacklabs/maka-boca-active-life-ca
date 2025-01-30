package io.reactivestax.activelife.domain.facility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "facilities")
public class Facilities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FACILITY_ID")
    private Long id;

    @Column(name = "NAME", length = 200)
    private String name;

    @Column(name = "STREET_NO", length = 10)
    private String streetNo;

    @Column(name = "STREET_NAME", length = 200)
    private String streetName;

    @Column(name = "CITY", length = 100)
    private String city;

    @Column(name = "PROVINCE", length = 100)
    private String province;

    @Column(name = "COUNTRY", length = 50)
    private String country;

    @Column(name = "POSTAL_CODE", length = 7)
    private String postalCode;

    @Column(name = "DESCRIPTION", length = 400)
    private String description;

    @Column(name = "CREATED_TS")
    private LocalDate createdTs;

    @Column(name = "LAST_UPDATED_TS")
    private LocalDate lastUpdatedTs;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @OneToMany(mappedBy = "facility")
    private List<FacilityFunctions> facilityFunctions;
}
