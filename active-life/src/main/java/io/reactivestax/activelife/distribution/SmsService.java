package io.reactivestax.activelife.distribution;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import io.reactivestax.activelife.dto.FamilyMemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;


    public void sendSms(String phone, String message) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new com.twilio.type.PhoneNumber("+13657781555"),
                new com.twilio.type.PhoneNumber("+19187719571"),
                message
        ).create();
    }
}
