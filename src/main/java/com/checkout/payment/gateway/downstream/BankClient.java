package com.checkout.payment.gateway.downstream;

import com.checkout.payment.gateway.exception.BankingSystemUnavailableException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
/**
 * HTTP client responsible for communicating with the downstream banking system.
 *
 * <p>Wraps {@link RestTemplate} to submit payment authorization requests to the
 * bank's payment endpoint. All connectivity and server-side errors are caught
 * and rethrown as {@link BankingSystemUnavailableException} to decouple the
 * rest of the application from Spring's HTTP exception hierarchy.
 *
 */
@Component
public class BankClient {

  private static final Logger LOG = LoggerFactory.getLogger(BankClient.class);

  private final RestTemplate restTemplate;

  public BankClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Submits a payment authorisation request to the downstream banking system.
   *
   * @param request the payment details to send to the bank; must not be null
   * @return the {@link BankPaymentResponse} containing authorisation result
   * @throws BankingSystemUnavailableException if the bank is unreachable or
   *         returns a server error response
   */
  public BankPaymentResponse processPayment(BankPaymentRequest request) {
    LOG.info("Requesting auth from bank client");
    try {
      return restTemplate.postForObject("http://localhost:8080/payments", request,
          BankPaymentResponse.class);
    } catch (ResourceAccessException e) {

      LOG.error("Bank is unreachable: {}", e.getMessage());
      throw new BankingSystemUnavailableException("Error processing payment");

    } catch (HttpServerErrorException e) {
      LOG.error("Bank returned error status: {}", e.getStatusCode());
      throw new BankingSystemUnavailableException("Error processing payment");
    }
  }

}
