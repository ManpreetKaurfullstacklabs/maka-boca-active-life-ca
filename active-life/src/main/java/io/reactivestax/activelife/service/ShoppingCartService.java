package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.OfferedCoursesShoppingCartDTO;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ShoppingCartService {

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    private final Map<Long, Map<Long, String>> userCarts = new HashMap<>();

    @CachePut(value = "cartItems", key = "#shoppingCartDTO.familyMemberId")
    public Map<Long, String> addToCart(ShoppingCartDTO shoppingCartDTO) {
        Optional<OfferedCourses> byId = offeredCourseRepository.findById(shoppingCartDTO.getOfferedCourseId());
        OfferedCourses offeredCourses = byId.get();
        Long cost = offeredCourses.getCost();
        Long familyMemberId = shoppingCartDTO.getFamilyMemberId();
        Long offeredCourseId = shoppingCartDTO.getOfferedCourseId();

        String cartItemDetails = "Course ID: " + offeredCourseId + ", Quantity: " + shoppingCartDTO.getNoOfItems() + ", Price: " + cost;
        userCarts.computeIfAbsent(familyMemberId, k -> new HashMap<>()).put(offeredCourseId, cartItemDetails);
        return userCarts.get(familyMemberId);
    }


    @CachePut(value = "cartItems", key = "#familyMemberId")
    public Map<Long, String> getCart(Long familyMemberId) {
        System.out.println("Fetching cart for user: " + familyMemberId);
        return userCarts.getOrDefault(familyMemberId, new HashMap<>());
    }

    @CachePut(value = "cartItems", key = "#familyMemberId")
    public Map<Long, String> getCartOfMembers(Long familyMemberId) {
        System.out.println("Fetching cart for user: " + familyMemberId);
        return userCarts.getOrDefault(familyMemberId, new HashMap<>());
    }

    @CachePut(value = "cartItems", key = "#familyMemberId")
    public Map<Long, String> deleteFromUser(Long familyMemberId) {
        System.out.println("deleting cart for user: " + familyMemberId);
        return  userCarts.remove(familyMemberId);
    }

}
