package io.reactivestax.activelife.service;

import io.reactivestax.activelife.Enums.PaymentStatus;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.dto.PaymentRequestDTO;
import io.reactivestax.activelife.dto.PaymentResponseDTO;
import io.reactivestax.activelife.repository.courses.OfferedCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockPaymentService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    private  OfferedCourseRepository offeredCourseRepository;




    public PaymentResponseDTO processPayment(PaymentRequestDTO paymentRequest) {
        PaymentResponseDTO response = new PaymentResponseDTO();
        Long offeredCourseId = offeredCourseRepository.findById(paymentRequest.getOfferedCourseId()).get().getOfferedCourseId();
        if(!offeredCourseRepository.existsById(offeredCourseId)){
            response.setTransactionId(UUID.randomUUID().toString());
            response.setStatus(PaymentStatus.FAILED);
            response.setMessage("Payment Failed");
            response.setAmount(paymentRequest.getAmount());
            response.setFamilyMemberId(paymentRequest.getFamilyMemberId());
        }
        else {
            response.setTransactionId(UUID.randomUUID().toString());
            response.setStatus(PaymentStatus.SUCCESS);
            response.setMessage("Payment processed successfully.");
            response.setAmount(paymentRequest.getAmount());
            response.setFamilyMemberId(paymentRequest.getFamilyMemberId());
         }

        shoppingCartService.deleteFromUser(paymentRequest.getFamilyMemberId());
        return response;
    }
}
