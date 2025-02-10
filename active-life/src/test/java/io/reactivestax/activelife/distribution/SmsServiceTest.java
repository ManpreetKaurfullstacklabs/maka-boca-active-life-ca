package io.reactivestax.activelife.distribution;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import io.reactivestax.activelife.utility.distribution.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testSendSms_Success() {
        try (MockedStatic<Message> mockStatic = mockStatic(Message.class)) {
            MessageCreator messageCreatorMock = mock(MessageCreator.class);
            Message mockMessage = mock(Message.class);
            mockStatic.when(() ->
                    Message.creator(any(com.twilio.type.PhoneNumber.class),
                            any(com.twilio.type.PhoneNumber.class),
                            anyString())
            ).thenReturn(messageCreatorMock);
            when(messageCreatorMock.create()).thenReturn(mockMessage);

            String phoneNumber = "+1234567890";
            smsService.sendSms(phoneNumber, "Test message");
            verify(messageCreatorMock, times(1)).create();
        }
    }


    @Test
    void testSendVerificationLink_Success() {
        try (MockedStatic<Message> mockStatic = mockStatic(Message.class)) {
            MessageCreator messageCreatorMock = mock(MessageCreator.class);
            Message mockMessage = mock(Message.class);
            mockStatic.when(() ->
                    Message.creator(any(com.twilio.type.PhoneNumber.class),
                            any(com.twilio.type.PhoneNumber.class),
                            anyString())
            ).thenReturn(messageCreatorMock);
            when(messageCreatorMock.create()).thenReturn(mockMessage);

            String phoneNumber = "+1234567890";
            smsService.verificationLink(phoneNumber, "Test message");
            verify(messageCreatorMock, times(1)).create();
        }
    }

}
