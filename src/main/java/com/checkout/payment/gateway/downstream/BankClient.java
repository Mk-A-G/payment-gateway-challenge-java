package com.checkout.payment.gateway.downstream;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class BankClient {

  private static final Logger LOG = LoggerFactory.getLogger(BankClient.class);
  @Autowired
  private final RestTemplate restTemplate;

  public BankClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public BankPaymentResponse processPayment(BankPaymentRequest request) {
    LOG.info("Requesting auth from bank client");
    try {
      return restTemplate.postForObject("http://localhost:8080/payments", request,
          BankPaymentResponse.class);
    } catch (ResourceAccessException e) {

      LOG.error("Bank is unreachable: {}", e.getMessage());
      throw new EventProcessingException("Error processing payment");

    } catch (HttpServerErrorException e) {
      LOG.error("Bank returned error status: {}", e.getStatusCode());
      throw new EventProcessingException("Error processing payment");
    }
  }

}
