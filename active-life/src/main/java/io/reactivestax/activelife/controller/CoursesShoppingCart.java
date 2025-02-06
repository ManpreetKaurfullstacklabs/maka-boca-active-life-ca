package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCoursesShoppingCartDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoppingcart")
public class CoursesShoppingCart {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @PostMapping("/waitlist")
    public  ResponseEntity<String> addToWaitList (@RequestBody OfferedCoursesShoppingCartDTO shoppingCartDTO){
        String reponse = familyCourseRegistrationService.handleWaitlist(shoppingCartDTO.getFamilyMemberId(), shoppingCartDTO.getOfferedCourseId());
        return ResponseEntity.ok(reponse);
    }

    @PostMapping("add-to-cart")
    public ResponseEntity<String> addToCart(OfferedCoursesShoppingCartDTO offeredCoursesShoppingCartDTO) {
        shoppingCartService.getCart(offeredCoursesShoppingCartDTO.getFamilyMemberId());

        return ResponseEntity.ok("Course saved succesffuly");
    }



}
