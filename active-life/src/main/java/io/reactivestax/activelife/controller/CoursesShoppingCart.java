package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.OfferedCoursesShoppingCartDTO;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    // ✅ Get List of Courses for a Member
    @GetMapping("/getcourses/{familyMemberId}")
    public ResponseEntity<List<ShoppingCartDTO>> getCourses(@PathVariable Long familyMemberId) {
        List<ShoppingCartDTO> courses = shoppingCartService.getCoursesForMember(familyMemberId);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses);
    }

//    @GetMapping("/getcache/{familyMemberId}")
//    public ResponseEntity<Map<Long, String>> getCache(@PathVariable Long familyMemberId) {
//        Map<Long, String> cart = shoppingCartService.getCart(familyMemberId);
//        if (cart.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of());
//        }
//
//        return ResponseEntity.ok(cart);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFromCache(@PathVariable Long familyMemberId){
        shoppingCartService.deleteFromUser(familyMemberId);

        return ResponseEntity.ok("deleted");
    }

}
