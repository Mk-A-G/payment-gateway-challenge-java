package com.checkout.payment.gateway.downstream;

import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BankClient {

  @Autowired
  private final RestTemplate restTemplate;

  public BankClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public BankPaymentResponse processPayment(BankPaymentRequest request) {
    return restTemplate.postForObject("",null, BankPaymentResponse.class);
  }

}
