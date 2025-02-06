//package io.reactivestax.activelife.distribution;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import io.reactivestax.activelife.utility.distribution.SmsService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jms.core.JmsTemplate;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class SmsServiceTest {
//
//    @InjectMocks
//    private SmsService smsService;
//
//    @Mock
//    private JmsTemplate jmsTemplate;
//
//    private MockedStatic<Twilio> twilioMockedStatic;
//
//    @BeforeEach
//    public void setUp() {
//        // Mock the static method Twilio.init()
//        twilioMockedStatic = mockStatic(Twilio.class);
//        twilioMockedStatic.when(() -> Twilio.init(any(), any())).thenReturn(null);  // Mock static init method
//    }
//
//    @Test
//    public void testVerificationLink() {
//        // Arrange
//        String phone = "+123456789";
//        String messageContent = "Your verification link is: http://example.com/verify";
//
//        // Create a mock Message object to return
//        Message mockMessage = mock(Message.class);
//
//        // Mock static method Message.creator using Mockito.mockStatic
//        try (MockedStatic<Message> messageMockedStatic = mockStatic(Message.class)) {
//
//            smsService.verificationLink(phone, messageContent);
//
//            ArgumentCaptor<com.twilio.type.PhoneNumber> phoneNumberCaptor = ArgumentCaptor.forClass(com.twilio.type.PhoneNumber.class);
//            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
//            verify(Message.creator(phoneNumberCaptor.capture(), phoneNumberCaptor.capture(), messageCaptor.capture()));
//
//            // Validate that the correct phone number and message content were passed
//            assert phoneNumberCaptor.getValue().toString().equals("+123456789");
//            assert messageCaptor.getValue().equals(messageContent);
//        }
//    }
//
//    @Test
//    public void testSendSms() {
//        // Arrange
//        String phone = "+987654321";
//        String messageContent = "This is a test message.";
//
//        // Create a mock Message object to return
//        Message mockMessage = mock(Message.class);
//
//        // Mock static method Message.creator using Mockito.mockStatic
//        try (MockedStatic<Message> messageMockedStatic = mockStatic(Message.class)) {
//     //       messageMockedStatic.when(() -> Message.creator(any(), any(), any())).thenReturn(mockMessage);
//
//            // Act
//            smsService.sendSms(phone, messageContent);
//
//            // Assert
//        //    verify(Message.creator(any(), any(), any()), times(1)).create();
//
//            // Capture the arguments passed to Message.creator
//            ArgumentCaptor<com.twilio.type.PhoneNumber> phoneNumberCaptor = ArgumentCaptor.forClass(com.twilio.type.PhoneNumber.class);
//            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
//            verify(Message.creator(phoneNumberCaptor.capture(), phoneNumberCaptor.capture(), messageCaptor.capture()));
//
//            // Validate that the correct phone number and message content were passed
//            assert phoneNumberCaptor.getValue().toString().equals("+987654321");
//            assert messageCaptor.getValue().equals(messageContent);
//        }
//    }
//
//    @AfterEach
//    public void tearDown() {
//        // Close the static mocks after each test
//        twilioMockedStatic.close();
//    }
//}
