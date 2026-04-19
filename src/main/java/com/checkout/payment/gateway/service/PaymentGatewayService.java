package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.downstream.BankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;

  private final BankClient bankClient;



  public PaymentGatewayService(PaymentsRepository paymentsRepository, BankClient bankClient) {
    this.paymentsRepository = paymentsRepository;
    this.bankClient = bankClient;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.info("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    LOG.info("Requesting payment Auth to payment with pan last 4 {}", extractLastFourDigitsFromPan(paymentRequest.getCardNumber()));
    BankPaymentResponse response = callBankForAuth(paymentRequest);
    LOG.info("Payment Auth Response: {}", response);

    return  constructPostPaymentResponse(paymentRequest, response);

  }

  private String extractLastFourDigitsFromPan(String cardNumberLastFour) {
    return cardNumberLastFour.substring(cardNumberLastFour.length() - 4);
  }

  private BankPaymentResponse callBankForAuth(PostPaymentRequest paymentRequest){
    return bankClient.processPayment(BankPaymentRequest.builder()
        .cardNumber(paymentRequest.getCardNumber())
        .expiryDate(paymentRequest.getExpiryDate())
        .currency(paymentRequest.getCurrency())
        .amount(paymentRequest.getAmount())
        .cvv(paymentRequest.getCvv())
        .build());
  }

  private PostPaymentResponse constructPostPaymentResponse(PostPaymentRequest paymentRequest,BankPaymentResponse response) {
    return PostPaymentResponse.builder()
        .id(UUID.randomUUID())
        .amount(paymentRequest.getAmount())
        .expiryYear(paymentRequest.getExpiryYear())
        .expiryMonth(paymentRequest.getExpiryMonth())
        .currency(paymentRequest.getCurrency())
        .cardNumberLastFour(extractLastFourDigitsFromPan(paymentRequest.getCardNumber()))
        .status(response.isAuthorized()? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED)
        .build();
  }

}
