package io.reactivestax.activelife.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.PaymentStatus;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.dto.PaymentRequestDTO;
import io.reactivestax.activelife.dto.PaymentResponseDTO;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class MockPaymentServiceTest {

    @InjectMocks
    private MockPaymentService mockPaymentService;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private OfferedCourseRepository offeredCourseRepository;

    @Mock
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Mock
    private MemberRegistrationRepository memberRegistrationRepository;

    private PaymentRequestDTO paymentRequest;
    private ShoppingCartDTO cartItem;
    private OfferedCourses offeredCourse;
    private MemberRegistration memberRegistration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentRequest = new PaymentRequestDTO();
        paymentRequest.setFamilyMemberId(1L);
        paymentRequest.setAmount(100.0);

        cartItem = new ShoppingCartDTO();
        cartItem.setOfferedCourseId(1L);
        cartItem.setPrice(100L);

        offeredCourse = new OfferedCourses();
        offeredCourse.setOfferedCourseId(1L);
        offeredCourse.setNoOfSeats(10L);
        offeredCourse.setCost(100L);

        memberRegistration = new MemberRegistration();
        memberRegistration.setFamilyMemberId(1L);
        Map<Long, ShoppingCartDTO> cartItems = new HashMap<>();
        cartItems.put(1L, cartItem);
        when(shoppingCartService.getCart(paymentRequest.getFamilyMemberId())).thenReturn(cartItems);

        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.of(offeredCourse));
        when(memberRegistrationRepository.findById(1L)).thenReturn(Optional.of(memberRegistration));
    }

    @Test
    void testProcessPayment_EmptyCart() {

        Map<Long, ShoppingCartDTO> emptyCart = new HashMap<>();
        when(shoppingCartService.getCart(paymentRequest.getFamilyMemberId())).thenReturn(emptyCart);
        PaymentResponseDTO response = mockPaymentService.processPayment(paymentRequest);
        assertEquals("Payment Failed: No courses in cart.", response.getMessage());
        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    @Test
    void testProcessPayment_CourseNotFound() {
        when(offeredCourseRepository.findById(1L)).thenReturn(Optional.empty());
        PaymentResponseDTO response = mockPaymentService.processPayment(paymentRequest);
        assertEquals("Payment Failed: Course ID 1 does not exist.", response.getMessage());
        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    @Test
    void testProcessPayment_AmountMismatch() {

        paymentRequest.setAmount(150.0);
        PaymentResponseDTO response = mockPaymentService.processPayment(paymentRequest);

        assertEquals("Payment Failed: Incorrect amount. Expected: $100.0, Provided: $150.0", response.getMessage());
        assertEquals(PaymentStatus.FAILED, response.getStatus());
    }

    @Test
    void testProcessPayment_Success() {
        PaymentResponseDTO response = mockPaymentService.processPayment(paymentRequest);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals("Payment processed successfully.", response.getMessage());
    }

    @Test
    void testAddedToRegistration_NoCoursesInCart() {
        Map<Long, ShoppingCartDTO> emptyCart = new HashMap<>();
        when(shoppingCartService.getCart(paymentRequest.getFamilyMemberId())).thenReturn(emptyCart);
        String result = mockPaymentService.addedToRegistration(paymentRequest);
        assertEquals("Enrollment failed: No courses found in cart.", result);
    }

    @Test
    void testAddedToRegistration_Success() {
        when(familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(memberRegistration, offeredCourse))
                .thenReturn(Optional.empty());

        String result = mockPaymentService.addedToRegistration(paymentRequest);

        assertEquals("Successfully enrolled in courses.", result);
    }

    @Test
    void testAddedToRegistration_AlreadyEnrolled() {

        FamilyCourseRegistrations existingRegistration = new FamilyCourseRegistrations();
        existingRegistration.setIsWithdrawn(IsWithdrawn.NO);

        when(familyCourseRegistrationRepository.findByFamilyMemberIdAndOfferedCourseId(memberRegistration, offeredCourse))
                .thenReturn(Optional.of(existingRegistration));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mockPaymentService.addedToRegistration(paymentRequest);
        });

        assertEquals("Already enrolled in course " + offeredCourse.getOfferedCourseId(), exception.getMessage());
    }

}
