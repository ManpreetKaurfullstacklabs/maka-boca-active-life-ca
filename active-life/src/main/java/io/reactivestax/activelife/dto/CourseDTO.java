package io.reactivestax.activelife.dto;

import io.reactivestax.activelife.domain.agegroup.AgeGroups;
import io.reactivestax.activelife.domain.course.Subcategories;

import lombok.Data;



@Data
public class CourseDTO {


    private Long courseId;


    private String name;


    private String description;


    private Subcategories subcategories;


    private AgeGroups ageGroups;

}
