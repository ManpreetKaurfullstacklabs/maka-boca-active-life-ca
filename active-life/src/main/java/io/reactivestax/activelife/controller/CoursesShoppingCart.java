package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.Enums.PaymentStatus;
import io.reactivestax.activelife.dto.*;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.MockPaymentService;
import io.reactivestax.activelife.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
            shoppingCartService.addToCart(shoppingCartDTO);
            return ResponseEntity.ok("Course added to cart successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("Course is already in the cart.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to add to cart.");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = mockPaymentService.processPayment(paymentRequest);

        if (response.getStatus() == PaymentStatus.FAILED) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
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
    @DeleteMapping("/delete/{familyMemberId}/{offeredCourseId}")
    public ResponseEntity<String> deleteFromCachebyOfferedCourseId(
            @PathVariable Long familyMemberId,
            @PathVariable Long offeredCourseId) {
        shoppingCartService.deletebyOfferedCourseId(familyMemberId, offeredCourseId);
        return ResponseEntity.ok("Course removed successfully");
    }

}
