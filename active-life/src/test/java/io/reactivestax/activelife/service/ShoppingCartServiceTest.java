package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyGroups;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.dto.ShoppingCartDTO;
import io.reactivestax.activelife.dto.ShoppingCartResponseDTO;
import io.reactivestax.activelife.exception.InvalidMemberIdException;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import io.reactivestax.activelife.repository.memberregistration.FamilyCourseRegistrationRepository;
import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartServiceTest {

    @Mock
    private OfferedCourseRepository offeredCourseRepository;

    @Mock
    private MemberRegistrationRepository memberRegistrationRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private FamilyCourseRegistrationRepository familyCourseRegistrationRepository;

    @Mock
    private Cache cartCache;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private Long familyMemberId;
    private Long offeredCourseId;
    private ShoppingCartDTO shoppingCartDTO;
    private OfferedCourses offeredCourses;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        FamilyGroups familyGroups = new FamilyGroups();
        familyGroups.setFamilyGroupId(1L);

        MemberRegistration memberRegistration = new MemberRegistration();
        memberRegistration.setStatus(Status.ACTIVE);
        memberRegistration.setFamilyGroupId(familyGroups);

        familyMemberId = 1L;
        offeredCourseId = 101L;
        shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setFamilyMemberId(familyMemberId);
        shoppingCartDTO.setOfferedCourseId(offeredCourseId);

        offeredCourses = new OfferedCourses();
        offeredCourses.setOfferedCourseId(offeredCourseId);
        offeredCourses.setCost(100L);
        offeredCourses.setNoOfSeats(10L);

        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(memberRegistration));
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.of(offeredCourses));
        when(cacheManager.getCache("cartItems")).thenReturn(cartCache);
    }


    @Test
    void addToCart_shouldAddCourseToCart() {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setFamilyMemberId(familyMemberId);
        shoppingCartDTO.setOfferedCourseId(offeredCourseId);
        shoppingCartService.addToCart(shoppingCartDTO);
        verify(cartCache).put(eq(familyMemberId), any(Map.class));
    }



    @Test
    void addToCart_shouldThrowExceptionIfMemberIsInactive() {
        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(new MemberRegistration()));
        assertThrows(NullPointerException.class, () -> shoppingCartService.addToCart(shoppingCartDTO));
    }
    @Test
    void getCart_shouldReturnCorrectCart() {

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setFamilyMemberId(familyMemberId);
        shoppingCartDTO.setOfferedCourseId(offeredCourseId);
        shoppingCartDTO.setPrice(100L);

        Map<Long, ShoppingCartDTO> cart = new HashMap<>();
        cart.put(offeredCourseId, shoppingCartDTO);

        Cache.ValueWrapper mockWrapper = mock(Cache.ValueWrapper.class);
        when(mockWrapper.get()).thenReturn(cart);
        when(cartCache.get(familyMemberId)).thenReturn(mockWrapper);

        Map<Long, ShoppingCartDTO> result = shoppingCartService.getCart(familyMemberId);

        assertEquals(1, result.size());
        assertEquals(shoppingCartDTO, result.get(offeredCourseId));
    }


    @Test
    void getCoursesForMember_shouldReturnCoursesAndTotalPrice() {

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setFamilyMemberId(familyMemberId);
        shoppingCartDTO.setOfferedCourseId(offeredCourseId);
        shoppingCartDTO.setPrice(100L);

        Map<Long, ShoppingCartDTO> cart = new HashMap<>();
        cart.put(offeredCourseId, shoppingCartDTO);

        Cache.ValueWrapper mockWrapper = mock(Cache.ValueWrapper.class);
        when(mockWrapper.get()).thenReturn(cart);
        when(cartCache.get(familyMemberId)).thenReturn(mockWrapper);
        ShoppingCartResponseDTO expectedResponse = new ShoppingCartResponseDTO(familyMemberId, List.of(shoppingCartDTO), 100.0);
        ShoppingCartResponseDTO result = shoppingCartService.getCoursesForMember(familyMemberId);

        assertEquals(expectedResponse, result);
    }

    @Test
    void deleteFromUser_shouldRemoveFromCache() {
        shoppingCartService.deleteFromUser(familyMemberId);
        verify(cartCache).evict(familyMemberId);
    }

    @Test
    void checkCourseIsAvailabeOrNot_shouldThrowExceptionWhenSeatsFull() {
        offeredCourses.setNoOfSeats(2L);
        when(familyCourseRegistrationRepository.countByOfferedCourseIdAndIsWithdrawn(offeredCourses, IsWithdrawn.NO)).thenReturn(2L);
        assertThrows(RuntimeException.class, () -> shoppingCartService.checkCourseIsAvailabeOrNot(offeredCourses));
    }

    @Test
    void addToCart_shouldThrowExceptionIfOfferedCourseIsNotFound() {
        MemberRegistration activeMember = new MemberRegistration();
        activeMember.setStatus(Status.ACTIVE);
        when(memberRegistrationRepository.findById(familyMemberId)).thenReturn(Optional.of(activeMember));
        when(offeredCourseRepository.findById(offeredCourseId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> shoppingCartService.addToCart(shoppingCartDTO));
    }




}
