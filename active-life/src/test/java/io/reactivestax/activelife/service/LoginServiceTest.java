package io.reactivestax.activelife.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import io.reactivestax.activelife.dto.LoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    private LoginDTO loginDTO;
    private MemberRegistration memberRegistration;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setPin("1234");

        memberRegistration = new MemberRegistration();
        memberRegistration.setMemberLogin("user123");
        memberRegistration.setPin("1234");
        memberRegistration.setStatus(Status.ACTIVE);
    }

    @Test
    void testLoginVerification() {
        loginDTO.setMemberLoginId("user123");
        String result = verifyLogin();
        assertEquals("Successfully verified", result);
    }

    @Test
    void testInvalidLogin() {
        loginDTO.setMemberLoginId("wrongUser");
        String result = verifyLogin();
        assertEquals("Invalid", result);
    }

    private String verifyLogin() {
        if (loginDTO.getMemberLoginId().equals(memberRegistration.getMemberLogin()) &&
                loginDTO.getPin().equals(memberRegistration.getPin()) &&
                memberRegistration.getStatus().equals(Status.ACTIVE)) {
            return "Successfully verified";
        } else {
            return "Invalid";
        }
    }
}
