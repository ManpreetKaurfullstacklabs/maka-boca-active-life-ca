package io.reactivestax.activelife.service;

import io.reactivestax.activelife.domain.membership.MemberRegistration;

import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppUserDetailsServiceImpl implements UserDetailsService {

    private final  MemberRegistrationRepository memberRegistrationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberRegistration user = memberRegistrationRepository.findByMemberLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getMemberLogin())
                .password(user.getPin())
                .authorities(user.getRole().name())
                .build();
    }
}
