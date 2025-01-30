package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.Enums.GroupOwner;
import io.reactivestax.activelife.Enums.PreferredMode;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.Login;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberDTO {

    private String memberName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String streetNo;
    private String streetName;
    private PreferredMode preferredMode;
    private String city;
    private String province;
    private String country;
    private String postalCode;
    private String homePhoneNo;
    private String bussinessPhoneNo;
    private String language;
    private Long memberLoginId;
    private GroupOwner groupOwner;
    private Status status;
    private FamilyGroups familyGroups;

}
