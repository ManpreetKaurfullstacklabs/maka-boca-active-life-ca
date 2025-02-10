package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.PaymentStatus;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.dto.PaymentRequestDTO;
import io.reactivestax.activelife.dto.PaymentResponseDTO;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    private MemberRegistrationRepository memberRegistrationRepository;

    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        Long familyMemberId = paymentRequest.getFamilyMemberId();
        double requestedAmount = paymentRequest.getAmount();

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

      //  shoppingCartService.deleteFromUser(familyMemberId);

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

    public String addedToRegistration(PaymentRequestDTO paymentRequestDTO) {
        Long familyMemberId = paymentRequestDTO.getFamilyMemberId();
        Map<Long, ShoppingCartDTO> cartItems = shoppingCartService.getCart(familyMemberId);

        if (cartItems.isEmpty()) {
            return "Enrollment failed: No courses found in cart.";
        }

        for (Map.Entry<Long, ShoppingCartDTO> entry : cartItems.entrySet()) {
            ShoppingCartDTO cartItem = entry.getValue();

            OfferedCourses offeredCourse = offeredCourseRepository.findById(cartItem.getOfferedCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            MemberRegistration memberRegistration = memberRegistrationRepository.findById(familyMemberId)
                    .orElseThrow(() -> new InvalidMemberIdException("Invalid family member ID"));

            Optional<FamilyCourseRegistrations> existingRegistration = familyCourseRegistrationRepository
                    .findByFamilyMemberIdAndOfferedCourseId(memberRegistration, offeredCourse );

            if (existingRegistration.isPresent() && existingRegistration.get().getIsWithdrawn().equals(IsWithdrawn.NO)) {
                throw new RuntimeException("Already enrolled in course " + offeredCourse.getOfferedCourseId());
            }
            FamilyCourseRegistrations familyCourseRegistrations = new FamilyCourseRegistrations();
            familyCourseRegistrations.setFamilyMemberId(memberRegistration);
            familyCourseRegistrations.setWithdrawnCredits(0L);
            familyCourseRegistrations.setOfferedCourseId(offeredCourse);
            familyCourseRegistrations.setIsWithdrawn(IsWithdrawn.NO);
            familyCourseRegistrations.setIsWaitListed(IsWaitListed.NO);
            familyCourseRegistrations.setEnrollmentDate(LocalDate.now());
            familyCourseRegistrations.setEnrollmentActorId(familyMemberId);
            familyCourseRegistrations.setNoOfseats(offeredCourse.getNoOfSeats());
            familyCourseRegistrations.setCost(cartItem.getPrice());
            familyCourseRegistrations.setCreatedBy(familyMemberId);
            familyCourseRegistrations.setCreatedAt(LocalDateTime.now());
            familyCourseRegistrations.setLastUpdateBy(familyMemberId);
            familyCourseRegistrations.setLastUpdatedTime(LocalDateTime.now());

            familyCourseRegistrationRepository.save(familyCourseRegistrations);
        }

        return "Successfully enrolled in courses.";
    }



}
