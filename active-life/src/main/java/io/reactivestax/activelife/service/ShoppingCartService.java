package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.dto.ShoppingCartResponseDTO;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShoppingCartService {

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private MemberRegistrationRepository memberRegistrationRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;



    public void addToCart(ShoppingCartDTO shoppingCartDTO) {
        Optional<OfferedCourses> offeredCoursesOpt = offeredCourseRepository.findById(shoppingCartDTO.getOfferedCourseId());

        Status status = memberRegistrationRepository.findById(shoppingCartDTO.getFamilyMemberId())
                .orElseThrow(() -> new InvalidMemberIdException("Member not found"))
                .getStatus();

        if (status.equals(Status.INACTIVE)) {
            throw new NullPointerException("Member is not active");
        }
        if (offeredCoursesOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid offered course ID");
        }

        OfferedCourses offeredCourses = offeredCoursesOpt.get();
        checkCourseIsAvailabeOrNot(offeredCourses );
        shoppingCartDTO.setPrice(offeredCourses.getCost());

        Cache cartCache = cacheManager.getCache("cartItems");
        if (cartCache != null) {
            Map<Long, ShoppingCartDTO> cart = getCart(shoppingCartDTO.getFamilyMemberId());

            if (cart.containsKey(shoppingCartDTO.getOfferedCourseId())) {
                throw new IllegalStateException("Course is already in the cart");
            }
            cart.put(shoppingCartDTO.getOfferedCourseId(), shoppingCartDTO);
            cartCache.put(shoppingCartDTO.getFamilyMemberId(), cart);
        }
    }

    public Map<Long, ShoppingCartDTO> getCart(Long familyMemberId) {
        Cache cartCache = cacheManager.getCache("cartItems");
        if (cartCache != null) {
            Cache.ValueWrapper valueWrapper = cartCache.get(familyMemberId);
            if (valueWrapper != null) {
                return (Map<Long, ShoppingCartDTO>) valueWrapper.get();
            }
        }
        return new HashMap<>();
    }

    public ShoppingCartResponseDTO getCoursesForMember(Long familyMemberId) {
        Map<Long, ShoppingCartDTO> cart = getCart(familyMemberId);
        List<ShoppingCartDTO> courses = new ArrayList<>(cart.values());
        double totalPrice = courses.stream().mapToDouble(ShoppingCartDTO::getPrice).sum();

        return new ShoppingCartResponseDTO(familyMemberId, courses, totalPrice);
    }

    public void deleteFromUser(Long familyMemberId) {
        Cache cartCache = cacheManager.getCache("cartItems");
        if (cartCache != null) {
            cartCache.evict(familyMemberId);
        }
    }

    public void deletebyOfferedCourseId(Long familyMemberId, Long offeredCourseId) {
        Cache cartCache = cacheManager.getCache("cartItems");
        if (cartCache != null) {

            Cache.ValueWrapper valueWrapper = cartCache.get(familyMemberId);
            if (valueWrapper != null) {
                Map<Long, ShoppingCartDTO> cart = (Map<Long, ShoppingCartDTO>) valueWrapper.get();

                if (cart.containsKey(offeredCourseId)) {
                    cart.remove(offeredCourseId);
                    cartCache.put(familyMemberId, cart);
                }
            }
        }
    }


    public void checkCourseIsAvailabeOrNot(OfferedCourses offeredCourse){
        Long availableSeats = offeredCourse.getNoOfSeats();
        Long enrolledCount = familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourse, IsWithdrawn.NO);
        if(enrolledCount.equals(availableSeats)){
            throw new RuntimeException("Course seats are full . Can't add to cart.");
        }

    }

}
