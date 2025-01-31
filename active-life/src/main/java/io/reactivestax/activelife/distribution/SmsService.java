package io.reactivestax.activelife.distribution;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;


    public void verificationLink(String phone, String message) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new com.twilio.type.PhoneNumber(phone),
                new com.twilio.type.PhoneNumber("+19187719571"),
                message
        ).create();
    }

    public void sendSms(String phone, String message) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new com.twilio.type.PhoneNumber(phone),
                new com.twilio.type.PhoneNumber("+19187719571"),
                message
        ).create();
    }
}
