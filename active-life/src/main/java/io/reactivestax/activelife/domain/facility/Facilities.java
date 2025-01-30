package io.reactivestax.activelife.domain.facility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "facilities")
public class Facitlities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FACILITY_ID")
    private Integer id;

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
    private Date createdTs;

    @Column(name = "LAST_UPDATED_TS")
    private Date lastUpdatedTs;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

    @OneToMany(mappedBy = "facility")
    private List<FacilityFunctions> facilityFunctions;
}
