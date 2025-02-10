//package io.reactivestax.activelife.controller;
//
//import io.reactivestax.activelife.dto.OfferedCoursesShoppingCartDTO;
//import io.reactivestax.activelife.dto.ShoppingCartDTO;
//import io.reactivestax.activelife.service.FamilyCourseRegistrationService;
//import io.reactivestax.activelife.service.ShoppingCartService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CoursesShoppingCart.class)
//class CoursesShoppingCartTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ShoppingCartService shoppingCartService;
//
//    @MockBean
//    private FamilyCourseRegistrationService familyCourseRegistrationService;
//
//    private ShoppingCartDTO shoppingCartDTO;
//
//    @BeforeEach
//    void setUp() {
//        shoppingCartDTO = new ShoppingCartDTO();
//        shoppingCartDTO.setFamilyMemberId(1L);
//        shoppingCartDTO.setOfferedCourseId(101L);
//        shoppingCartDTO.setPrice(150L);
//    }
//
//    @Test
//    void addToCart_ShouldReturnSuccess() throws Exception {
//        mockMvc.perform(post("/api/shoppingcart/add-to-cart")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"familyMemberId\":1, \"offeredCourseId\":101, \"price\":150}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("course are added to cart.."));
//
//        Mockito.verify(shoppingCartService, Mockito.times(1)).addToCart(any(ShoppingCartDTO.class));
//    }
//
//    @Test
//    void getCourses_ShouldReturnCoursesList() throws Exception {
//        List<ShoppingCartDTO> courses = Arrays.asList(shoppingCartDTO);
//        Mockito.when(shoppingCartService.getCoursesForMember(1L)).thenReturn(courses);
//
//        mockMvc.perform(get("/api/shoppingcart/getcourses/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].familyMemberId").value(1))
//                .andExpect(jsonPath("$[0].offeredCourseId").value(101))
//                .andExpect(jsonPath("$[0].price").value(150));
//
//        Mockito.verify(shoppingCartService, Mockito.times(1)).getCoursesForMember(1L);
//    }
//
//    @Test
//    void getCourses_ShouldReturnNotFound_WhenEmpty() throws Exception {
//        Mockito.when(shoppingCartService.getCoursesForMember(1L)).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/api/shoppingcart/getcourses/1"))
//                .andExpect(status().isNotFound());
//
//        Mockito.verify(shoppingCartService, Mockito.times(1)).getCoursesForMember(1L);
//    }
//
//    @Test
//    void deleteFromCache_ShouldReturnSuccess() throws Exception {
//        shoppingCartDTO = new ShoppingCartDTO();
//        shoppingCartDTO.setFamilyMemberId(2L);
//        shoppingCartDTO.setOfferedCourseId(101L);
//        shoppingCartDTO.setPrice(150L);
//
//        mockMvc.perform(delete("/api/shoppingcart/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("deleted"));
//
//        Mockito.verify(shoppingCartService, Mockito.times(1)).deleteFromUser(2L);
//    }
//
//    @Test
//    void addToWaitlist_ShouldReturnSuccess() throws Exception {
//        OfferedCoursesShoppingCartDTO waitlistDTO = new OfferedCoursesShoppingCartDTO();
//        waitlistDTO.setFamilyMemberId(1L);
//        waitlistDTO.setOfferedCourseId(101L);
//
//        Mockito.when(familyCourseRegistrationService.handleWaitlist(eq(1L), eq(101L)))
//                .thenReturn("Added to waitlist");
//
//        mockMvc.perform(post("/api/shoppingcart/waitlist")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"familyMemberId\":1, \"offeredCourseId\":101}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Added to waitlist"));
//
//        Mockito.verify(familyCourseRegistrationService, Mockito.times(1)).handleWaitlist(1L, 101L);
//    }
//}
