package io.reactivestax.activelife.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facitlities {
    private BigInteger facilityId;
    private String  streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String description;
    private Date createdTime;
    private Date lastUpdate;
}
