package io.reactivestax.activelife.controller;

import io.reactivestax.activelife.Enums.PaymentStatus;
import io.reactivestax.activelife.dto.*;
import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
import io.reactivestax.activelife.service.MockPaymentService;
import io.reactivestax.activelife.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CoursesShoppingCartTest {

    private MockMvc mockMvc;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private MockPaymentService mockPaymentService;

    @Mock
    private FamilyCourseRegistrationService familyCourseRegistrationService;

    @InjectMocks
    private CoursesShoppingCart coursesShoppingCart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(coursesShoppingCart).build();
    }

    @Test
    void addToWaitList() throws Exception {
        Long familyMemberId = 1L;
        Long offeredCourseId = 1L;
     //   OfferedCoursesShoppingCartDTO shoppingCartDTO = new OfferedCoursesShoppingCartDTO(familyMemberId, offeredCourseId);

        // Mock the service layer
        when(familyCourseRegistrationService.handleWaitlist(familyMemberId, offeredCourseId)).thenReturn("Course added to waitlist");

        mockMvc.perform(post("/api/shoppingcart/waitlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyMemberId\": " + familyMemberId + ", \"offeredCourseId\": " + offeredCourseId + "}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course added to waitlist"));
    }

    @Test
    void addToCart() throws Exception {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setFamilyMemberId(1L);
        shoppingCartDTO.setOfferedCourseId(1L);
        shoppingCartDTO.setPrice(100L);

        // Mock the service layer for void method
        doNothing().when(shoppingCartService).addToCart(shoppingCartDTO);

        mockMvc.perform(post("/api/shoppingcart/add-to-cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyMemberId\": 1, \"offeredCourseId\": 1, \"price\": 100.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course added to cart successfully."));
    }

    @Test
    void deleteFromCache() throws Exception {
        Long familyMemberId = 1L;

        // Mock the service layer for void method
        doNothing().when(shoppingCartService).deleteFromUser(familyMemberId);

        mockMvc.perform(delete("/api/shoppingcart/{familyMemberId}", familyMemberId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart deleted successfully."));
    }

    @Test
    void processPayment() throws Exception {
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
        paymentRequest.setFamilyMemberId(1L);
        paymentRequest.setAmount(200.0);

        PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
        paymentResponse.setStatus(PaymentStatus.SUCCESS);
        paymentResponse.setAmount(200.0);

        when(mockPaymentService.processPayment(any(PaymentRequestDTO.class))).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/shoppingcart/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"familyMemberId\": 1, \"amount\": 200.0}"))
                .andExpect(status().isOk());
    }

    @Test
    void getCourses() throws Exception {
        Long familyMemberId = 1L;

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setFamilyMemberId(familyMemberId);
        shoppingCartDTO.setOfferedCourseId(1L);
        shoppingCartDTO.setPrice(100L);

        List<ShoppingCartDTO> cartItems = List.of(shoppingCartDTO);

        ShoppingCartResponseDTO shoppingCartResponseDTO = new ShoppingCartResponseDTO(familyMemberId, cartItems, 100L);

        when(shoppingCartService.getCoursesForMember(familyMemberId)).thenReturn(shoppingCartResponseDTO);

        mockMvc.perform(get("/api/shoppingcart/getcourses/{familyMemberId}", familyMemberId))
                .andExpect(status().isOk());
    }



}
