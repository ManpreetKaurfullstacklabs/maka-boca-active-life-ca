package io.reactivestax.activelife.distribution;

import io.reactivestax.activelife.utility.distribution.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

class SmsServiceTest {
    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

}
