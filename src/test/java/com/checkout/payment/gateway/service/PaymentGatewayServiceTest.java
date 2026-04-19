package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.checkout.payment.gateway.downstream.BankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

  @Mock
  private PaymentsRepository paymentsRepository;

  @InjectMocks
  private PaymentGatewayService paymentGatewayService;

  private PostPaymentRequest request;

  private BankPaymentResponse bankPaymentResponse;

  @Mock
  private BankClient bankClient;

  @BeforeEach
  void setUp() {

    request = new PostPaymentRequest();
    request.setAmount(10);
    request.setCurrency("USD");
    request.setExpiryMonth(12);
    request.setExpiryYear(2026);
    request.setCardNumber("4321432143214321");
    request.setCvv("123");

    bankPaymentResponse = new BankPaymentResponse();
    bankPaymentResponse.setAuthorized(true);
    bankPaymentResponse.setAuthorizationCode(UUID.randomUUID().toString());

  }


  @Test
  void processPaymentReturnsPostPaymentResponse() {

    PostPaymentResponse response = paymentGatewayService.processPayment(request);
    assertNotNull(response);

  }

  @Test
  void processPaymentReturnsPostPaymentResponseWithGeneratedUUID() {

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertNotNull(response.getId());
    assertDoesNotThrow(() -> UUID.fromString(response.getId().toString()));
    assertEquals(request.getCurrency(), response.getCurrency());
    assertEquals(request.getAmount(), response.getAmount());

  }

  @Test
  void processPaymentReturnsPostPaymentResponseWithLastFourPAN() {

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertNotNull(response.getId());
    assertEquals(request.getCardNumber().substring(request.getCardNumber().length() - 4),
        response.getCardNumberLastFour());

  }

  @Test
  void processPaymentCallsBankDownStream() {

    paymentGatewayService.processPayment(request);

    Mockito.verify(bankClient).processPayment(Mockito.any());

  }

  @Test
  void processPaymentBankDownStreamCallReturnBankPaymentResponse() {

    Mockito.when(bankClient.processPayment(Mockito.any())).thenReturn(bankPaymentResponse);



    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    Mockito.verify(bankClient).processPayment(Mockito.any());

    assertEquals(response.getStatus().getName(),PaymentStatus.AUTHORIZED.getName());


  }

  @Test
  void processPaymentBankDownStreamCallIsNotAuthorised() {

    bankPaymentResponse.setAuthorized(false);

    Mockito.when(bankClient.processPayment(Mockito.any())).thenReturn(bankPaymentResponse);

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    Mockito.verify(bankClient).processPayment(Mockito.any());

    assertEquals(response.getStatus().getName(), PaymentStatus.DECLINED.getName());
  }

  @Test
  void processPaymentSavesPaymentInRepository() {

    Mockito.when(bankClient.processPayment(Mockito.any())).thenReturn(bankPaymentResponse);

    Mockito.verify(paymentsRepository).add(Mockito.any());

    paymentGatewayService.processPayment(request);

  }


  @Test
  void getPaymentByIdFindsPaymentInRepository() {

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertNotNull(response.getId());

    assertDoesNotThrow(() -> UUID.fromString(response.getId().toString()));

    assertEquals(request.getCurrency(), response.getCurrency());
    assertEquals(request.getAmount(), response.getAmount());
    assertEquals(request.getCardNumber(), response.getCardNumberLastFour());

  }

  @Test
  void getPaymentByIdDoesNotFindPaymentInRepository() {

    PostPaymentResponse response = paymentGatewayService.processPayment(request);

    assertNotNull(response.getId());

    assertDoesNotThrow(() -> UUID.fromString(response.getId().toString()));

    assertEquals(request.getCurrency(), response.getCurrency());
    assertEquals(request.getAmount(), response.getAmount());
    assertEquals(request.getCardNumber(), response.getCardNumberLastFour());

  }


}