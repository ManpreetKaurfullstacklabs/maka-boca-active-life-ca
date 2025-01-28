package io.reactivestax.activelife.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMember {

    private String memberName;
    private Date dob;
    private String gender;
    private String email;
    private String  streetNo;
    private String streetName;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String homePhoneNo;
    private String bussinessPhoneNo;
    private String language;
    private String memberLoginId;
    private Character isActive;
}
