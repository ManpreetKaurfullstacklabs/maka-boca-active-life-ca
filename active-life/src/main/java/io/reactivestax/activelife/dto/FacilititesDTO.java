package io.reactivestax.activelife.dto;

import jakarta.persistence.Column;
import lombok.Data;


import java.time.LocalDate;

@Data
public class FacilititesDTO {


    private Long id;


    private String name;


    private String streetNo;


    private String streetName;


    private String city;


    private String province;


    private String country;


    private String postalCode;


    private String description;


}
