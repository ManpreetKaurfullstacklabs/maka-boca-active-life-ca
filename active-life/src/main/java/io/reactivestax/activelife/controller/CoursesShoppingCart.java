package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.OfferedCourseDTO;
import io.reactivestax.activelife.dto.OfferedCoursesShoppingCartDTO;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/add-to-cart")
    public ResponseEntity<String> addToCart( @RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addToCart(shoppingCartDTO);
        return ResponseEntity.ok("course are added to cart..");
    }

    @GetMapping("/getcache/{familyMemberId}/{offeredCourseId}")
    public ResponseEntity<String> getCache(@PathVariable Long familyMemberId, @PathVariable Long offeredCourseId) {
        Map<Long, String> cart = shoppingCartService.getCart(familyMemberId);
        if (cart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart found for this family member.");
        }
        String cartItem = cart.get(offeredCourseId);
        if (cartItem != null) {
            return ResponseEntity.ok(cartItem);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart item not found for this course.");
        }
    }

    @GetMapping("/getcache/{familyMemberId}")
    public ResponseEntity<Map<Long, String>> getCache(@PathVariable Long familyMemberId) {
        Map<Long, String> cart = shoppingCartService.getCartOfMembers(familyMemberId);
        if (cart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of());
        }

        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<Long,String>> deleteFromCache(@PathVariable Long familyMemberId){
        Map<Long,String> cart = shoppingCartService.deleteFromUser(familyMemberId);
        if (cart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of());
        }
        return ResponseEntity.ok(cart);
    }

}
