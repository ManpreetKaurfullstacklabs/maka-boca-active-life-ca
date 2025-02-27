package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.Role;
import io.reactivestax.activelife.domain.membership.MemberRegistration;

import io.reactivestax.activelife.repository.memberregistration.MemberRegistrationRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {

    private final MemberRegistrationRepository familyMemberRepository;

    public AppUserDetailsServiceImpl(MemberRegistrationRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberRegistration familyMember = familyMemberRepository.findByMemberLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(familyMember.getMemberLogin())
                .password(familyMember.getPin())
                .authorities(Role.USER.name())
                .build();
    }
}
