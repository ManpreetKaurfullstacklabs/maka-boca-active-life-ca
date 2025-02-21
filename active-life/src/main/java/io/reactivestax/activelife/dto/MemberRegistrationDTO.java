package io.reactivestax.activelife.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.reactivestax.activelife.Enums.PreferredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRegistrationDTO {

   // @NotNull(message = "Member name is required")
  //  @NotEmpty(message = "Member name cannot be empty")
    private String memberName;

  //  @NotNull(message = "Date of birth is required")
  //  @JsonSerialize(using = LocalDateSerializer.class)
   // @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dob;

   // @NotNull(message = "Gender is required")
    private String gender;

   // @Email(message = "Email should be valid")
  //  @NotNull(message = "Email is required")
    private String email;

   // @NotNull(message = "Street number is required")
   // @NotEmpty(message = "Street number cannot be empty")
    private String streetNo;

   // @NotNull(message = "Street name is required")
   // @NotEmpty(message = "Street name cannot be empty")
    private String streetName;

   // @NotNull(message = "Preferred mode is required")
    private PreferredMode preferredMode;

   // @NotNull(message = "City is required")
   // @NotEmpty(message = "City cannot be empty")
    private String city;

  //  @NotNull(message = "Province is required")
   // @NotEmpty(message = "Province cannot be empty")
    private String province;

   // @NotNull(message = "Country is required")
   // @NotEmpty(message = "Country cannot be empty")
    private String country;

   // @NotNull(message = "Postal code is required")
   // @NotEmpty(message = "Postal code cannot be empty")
    private String postalCode;

   // @NotNull(message = "Home phone number is required")
   // @NotEmpty(message = "Home phone number cannot be empty")
    private String homePhoneNo;

  //  @NotNull(message = "Bussiness phone number is required")
    private String bussinessPhoneNo;

   // @NotNull(message = "Language is required")
    private String language;

   // @NotNull(message = "Member login ID is required")
   // @NotEmpty(message = "Member login ID cannot be empty")
    private String memberLoginId;

   // @NotNull(message = "Family group ID is required")
    private Long familyGroupId;
}
