package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.dto.*;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.MockPaymentService;
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
    private MockPaymentService mockPaymentService;

    @Autowired
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @PostMapping("/waitlist")
    public ResponseEntity<String> addToWaitList(@RequestBody OfferedCoursesShoppingCartDTO shoppingCartDTO) {
        String response = familyCourseRegistrationService.handleWaitlist(shoppingCartDTO.getFamilyMemberId(), shoppingCartDTO.getOfferedCourseId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<String> addToCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.addToCart(shoppingCartDTO);
        return ResponseEntity.ok("Course added to cart successfully.");
    }
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = mockPaymentService.processPayment(paymentRequest);
        mockPaymentService.addedToRegistration(paymentRequest);
        shoppingCartService.deleteFromUser(paymentRequest.getFamilyMemberId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getcourses/{familyMemberId}")
    public ResponseEntity<ShoppingCartResponseDTO> getCourses(@PathVariable Long familyMemberId) {
        ShoppingCartResponseDTO coursesForMember = shoppingCartService.getCoursesForMember(familyMemberId);
        return ResponseEntity.ok(coursesForMember);
    }

    @DeleteMapping("/{familyMemberId}")
    public ResponseEntity<String> deleteFromCache(@PathVariable Long familyMemberId) {
        shoppingCartService.deleteFromUser(familyMemberId);
        return ResponseEntity.ok("Cart deleted successfully.");
    }
}
