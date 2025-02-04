package io.reactivestax.activelife.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.reactivestax.activelife.Enums.GroupOwner;
import io.reactivestax.activelife.Enums.PreferredMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegistrationDTO {

    private String memberName;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
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
    private String memberLoginId;
    private GroupOwner groupOwner;
    private Long familyGroupId;

}
