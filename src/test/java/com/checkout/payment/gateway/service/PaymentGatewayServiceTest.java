package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

  @Mock
  private PaymentsRepository paymentsRepository;

  @InjectMocks
  private PaymentGatewayService paymentGatewayService;

  private PostPaymentRequest request;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    paymentGatewayService = new PaymentGatewayService(paymentsRepository);
    request = new PostPaymentRequest();


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

    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), String.class))
        .thenReturn("ftghyjrfj");

    paymentGatewayService.processPayment(request);

  }

  @Test
  void processPaymentBankDownStreamCallIsSuccess() {

    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), String.class))
        .thenReturn("ftghyjrfj");


    paymentGatewayService.processPayment(request);

  }

  @Test
  void processPaymentBankDownStreamCallIsNotAuthorised() {


    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), String.class))
        .thenReturn("ftghyjrfj");

    paymentGatewayService.processPayment(request);
  }

  @Test
  void processPaymentBankDownStreamIsDown() {

    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), String.class))
        .thenReturn(null);

    paymentGatewayService.processPayment(request);

  }

  @Test
  void processPaymentSavesPaymentInRepository() {

    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), String.class))
        .thenReturn("ftghyjrfj");

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