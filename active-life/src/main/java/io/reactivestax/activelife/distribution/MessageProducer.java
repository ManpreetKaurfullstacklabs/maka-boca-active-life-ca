package io.reactivestax.activelife.distribution;

import com.twilio.rest.api.v2010.account.Message;
import io.reactivestax.activelife.exception.JmsConsumingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;


@Component
public class MessageProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendToJmsEns(String phoneNo, String verificationLink ) {
        try {
            jmsTemplate.convertAndSend(phoneNo,verificationLink);

        } catch (JmsException e) {
            throw  new JmsConsumingException("error occur while producing to queue");
        }
    }

}
