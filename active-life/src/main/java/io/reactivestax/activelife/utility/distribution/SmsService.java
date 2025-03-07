package io.reactivestax.activelife.utility.distribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    private static final String BASE_URL = "http://localhost:8082/api/v1/ems/sms";
    private static final String OTP_URL=   "http://localhost:8082/api/v1/otp/sms";

    @Autowired
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SmsService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendSms(String phone, String message,String memberloginId) {

        //String customerId = "your-customer-id";
        Map<String, Object> smsRequest = new HashMap<>();
        smsRequest.put("customerId", memberloginId);
        smsRequest.put("message", message);
        smsRequest.put("recipient", phone);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(smsRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("SMS sent successfully via EMS API.");
        } else {
            System.err.println("Failed to send SMS via EMS API. Response: " + response.getStatusCode());
        }
    }

    public void verificationLink(String phone, String verificationLink , String memberLoginId) {
        String message = "Please verify your account by clicking on this link: " + verificationLink;
        sendSms(phone, message,memberLoginId);
    }


    public void sendOtpRequest(String customerContact, String memberLoginId) {

        Map<String, Object> otpRequest = new HashMap<>();
        otpRequest.put("customerId", memberLoginId);
        otpRequest.put("requestType", "SMS");
        otpRequest.put("phoneNo",customerContact);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(otpRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(OTP_URL, HttpMethod.POST, request, String.class);

        // Handle the response (for example, logging or checking the status)
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("OTP request sent successfully.");
        } else {
            System.err.println("Failed to send OTP request. Response: " + response.getStatusCode());
        }
    }
}
