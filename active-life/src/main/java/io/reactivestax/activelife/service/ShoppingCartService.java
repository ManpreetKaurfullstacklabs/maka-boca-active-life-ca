package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
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

    public void addToCart(ShoppingCartDTO shoppingCartDTO) {
        Optional<OfferedCourses> offeredCoursesOpt = offeredCourseRepository.findById(shoppingCartDTO.getOfferedCourseId());
        if (offeredCoursesOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid offered course ID");
        }

        OfferedCourses offeredCourses = offeredCoursesOpt.get();
        shoppingCartDTO.setPrice(offeredCourses.getCost());

        Cache cartCache = cacheManager.getCache("cartItems");
        if (cartCache != null) {
            Map<Long, ShoppingCartDTO> cart = getCart(shoppingCartDTO.getFamilyMemberId());
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


    public List<ShoppingCartDTO> getCoursesForMember(Long familyMemberId) {
        Map<Long, ShoppingCartDTO> cart = getCart(familyMemberId);
        return new ArrayList<>(cart.values());
    }


    public void deleteFromUser(Long familyMemberId) {
        Cache cartCache = cacheManager.getCache("cartItems");
        if (cartCache != null) {
            cartCache.evict(familyMemberId);
        }
    }
}
