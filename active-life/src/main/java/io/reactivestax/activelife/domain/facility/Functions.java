package io.reactivestax.activelife.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Functions {
    private BigInteger functionId;
    private  String description;
    private Date createTimeStamp;
    private  Date lastUpdatedAt;
    private  BigInteger createdBy;
    private BigInteger lastUpdatedBy;
}
