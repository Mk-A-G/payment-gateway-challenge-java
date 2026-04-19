package com.checkout.payment.gateway.downstream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.checkout.payment.gateway.exception.BankingSystemUnavailableException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BankClientTest {

  @Mock
  RestTemplate restTemplate;

  @InjectMocks
  BankClient bankClient;


  @Test
  void processPaymentThrowsExceptionWhenBankIsDown() {
    Mockito
        .when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(BankPaymentRequest.class),
                Mockito.eq(BankPaymentResponse.class)))
        .thenThrow(new ResourceAccessException("Testing here for Bank completely down"));

    Exception exception = assertThrows(BankingSystemUnavailableException.class, () -> {
      bankClient.processPayment(BankPaymentRequest.builder().build());
    });

    String expectedMessage = "Error processing payment";
    String actualMessage = exception.getMessage();
    System.out.println(actualMessage);
    System.out.println(expectedMessage);
    assertTrue(actualMessage.contains(expectedMessage));

  }

  @Test
  void processPaymentThrowsExceptionWhenBankIsUnavailable() {
    Mockito
        .when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(BankPaymentRequest.class),
            Mockito.eq(BankPaymentResponse.class)))
        .thenThrow(new HttpServerErrorException(HttpStatusCode.valueOf(503)));

    Exception exception = assertThrows(BankingSystemUnavailableException.class, () -> {
      bankClient.processPayment(BankPaymentRequest.builder().build());
    });

    String expectedMessage = "Error processing payment";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }
}