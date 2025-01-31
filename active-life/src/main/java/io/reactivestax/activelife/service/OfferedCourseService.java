package io.reactivestax.activelife.service;

import io.reactivestax.activelife.repository.OfferedCoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferedCourseService {

    @Autowired
    OfferedCoursesRepository offeredCoursesRepository;



}
