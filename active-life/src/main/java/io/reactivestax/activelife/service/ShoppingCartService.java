package io.reactivestax.activelife.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShoppingCartService {


        private final Map<Long, Map<Long, String>> userCarts = new HashMap<>();


        @Cacheable(value = "cartItems", key = "#userId")
        public Map<Long, String> getCart(Long familyMamberId) {
            System.out.println("Fetching cart for user: " + familyMamberId);
            return userCarts.getOrDefault(familyMamberId, new HashMap<>());
        }



}
