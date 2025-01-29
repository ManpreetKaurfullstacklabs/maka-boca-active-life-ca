package io.reactivestax.activelife.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityFunctions {
    private BigInteger functionId;
    private BigInteger facilityId;
    private Date createdTimestamp;
    private  Date lastupdatedTimestamp;
    private  BigInteger createdBy;
    private  BigInteger lastUpdatedBy;


}
