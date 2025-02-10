package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.PaymentStatus;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.PaymentRequestDTO;
import io.reactivestax.activelife.dto.PaymentResponseDTO;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Service
public class MockPaymentService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        Long familyMemberId = paymentRequest.getFamilyMemberId();
        double requestedAmount = paymentRequest.getAmount(); // 💰 User-provided amount

        Map<Long, ShoppingCartDTO> cartItems = shoppingCartService.getCart(familyMemberId);

        if (cartItems.isEmpty()) {
            return createFailedResponse("Payment Failed: No courses in cart.", familyMemberId, 0.0);
        }

        double totalAmount = 0.0;

        for (Long courseId : cartItems.keySet()) {
            Optional<OfferedCourses> offeredCourseOpt = offeredCourseRepository.findById(courseId);

            if (offeredCourseOpt.isEmpty()) {
                return createFailedResponse("Payment Failed: Course ID " + courseId + " does not exist.", familyMemberId, totalAmount);
            }

            OfferedCourses offeredCourse = offeredCourseOpt.get();

            checkCourseAvailability(offeredCourse);

            totalAmount += offeredCourse.getCost();
        }

        if (Double.compare(requestedAmount, totalAmount) != 0) {
            return createFailedResponse("Payment Failed: Incorrect amount. Expected: $" + totalAmount + ", Provided: $" + requestedAmount,
                    familyMemberId, requestedAmount);
        }

        response.setTransactionId(UUID.randomUUID().toString());
        response.setStatus(PaymentStatus.SUCCESS);
        response.setMessage("Payment processed successfully.");
        response.setAmount(totalAmount);
        response.setFamilyMemberId(familyMemberId);

        shoppingCartService.deleteFromUser(familyMemberId);

        return response;
    }
    private PaymentResponseDTO createFailedResponse(String message, Long familyMemberId, double amount) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setTransactionId(UUID.randomUUID().toString());
        response.setStatus(PaymentStatus.FAILED);
        response.setMessage(message);
        response.setAmount(amount);
        response.setFamilyMemberId(familyMemberId);
        return response;
    }
    private void checkCourseAvailability(OfferedCourses offeredCourse) {
        Long availableSeats = offeredCourse.getNoOfSeats();
        Long enrolledCount = familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO);

        if (enrolledCount >= availableSeats) {
            throw new RuntimeException("Payment Failed: Course " + offeredCourse.getOfferedCourseId() + " is full.");
        }
    }
}
