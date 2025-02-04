package io.reactivestax.activelife.domain.membership;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.reactivestax.activelife.Enums.GroupOwner;
import io.reactivestax.activelife.Enums.PreferredMode;
import io.reactivestax.activelife.Enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "family_registration")
public class MemberRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_member_id")
    private Long familyMemberId;

    @Column(name = "family_member_name")
    private String memberName;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email")
    private String email;

    @Column(name="prefered_mode")
    @Enumerated(EnumType.STRING)
    private PreferredMode preferredMode;

    @Column(name = "street_no")
    private String  streetNo;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "country")
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "home_phone_no")
    private String homePhoneNo;

    @Column(name = "bussiness_phone_no")
    private String bussinessPhoneNo;

    @Column(name = "language")
    private String language;

    @Column(name = "password")
    private String pin;

    @Column(name = "member_login_id",unique = true)
    private String memberLogin;

    @Column(name = "is_active")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name="is_group_owner")
    @Enumerated(EnumType.STRING)
    private GroupOwner groupOwner;

    @Column(name="uuid", unique = true)
    private String verificationUUID;

    @Column(name = "otp")
    private String otp;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "family_group_id")
    private  FamilyGroups familyGroupId;

}
