package io.reactivestax.activelife.domain.facility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "facility_functions")
public class FacilityFunctions {

    @EmbeddedId
    private FacilityFunctionId id;

    @ManyToOne
    @MapsId("functionId")
    @JoinColumn(name = "FUNCTION_ID", nullable = false)
    private Functions function;

    @ManyToOne
    @MapsId("facilityId")
    @JoinColumn(name = "facility_id", nullable = false)
    private Facilities facility;

    @Column(name = "CREATED_TS")
    private Date createdTs;

    @Column(name = "LAST_UPDATED_TS")
    private Date lastUpdatedTs;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "LAST_UPDATED_BY")
    private Long lastUpdatedBy;

}
