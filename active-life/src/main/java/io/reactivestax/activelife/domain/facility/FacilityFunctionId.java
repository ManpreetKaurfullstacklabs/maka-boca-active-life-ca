package io.reactivestax.activelife.domain.facility;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class FacilityFunctionId implements Serializable {

    @Column(name = "function_id")
    private Long functionId;

    @Column(name = "facility_id")
    private Long facilityId;
}
