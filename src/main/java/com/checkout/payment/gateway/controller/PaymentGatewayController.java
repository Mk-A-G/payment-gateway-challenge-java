package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling payment gateway operations.
 *
 * <p>Exposes endpoints for submitting new payments and retrieving
 * existing payment records by ID. All responses are serialized as JSON.
 *
 * <p>Base path: {@code /payment}
 */

@RestController("api")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  /**
   * Retrieves a payment record by its unique identifier.
   *
   * @param id Unique UUID for the request traceability. It is unique to each transaction. This
   *           endpoint is responsible for retrieving already persisted entries. Must not be blank
   * @return a {@link ResponseEntity} containing the {@link PostPaymentResponse} with HTTP 200 if
   * found
   * @throws com.checkout.payment.gateway.exception.EventProcessingException if no payment exists
   *                                                                         for the given ID
   *                                                                         resulting in an HTTP
   *                                                                         404 error
   */
  @GetMapping("/payment/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(
      @PathVariable @NotBlank UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  /**
   * Adds a payment request.
   *
   * @param paymentRequest A request containing valid card information.
   * @return a {@link ResponseEntity} containing the {@link PostPaymentResponse} with HTTP 200> Will
   * generate an id and return a status from the bank
   * @throws com.checkout.payment.gateway.exception.BankingSystemUnavailableException if the bank is
   *                                                                                  down resulting
   *                                                                                  in a 503 HTTP
   *                                                                                  code
   * @throws org.springframework.web.bind.MethodArgumentNotValidException             if the
   *                                                                                  contents of
   *                                                                                  the request
   *                                                                                  payload are
   *                                                                                  not valid
   *                                                                                  resulting in
   *                                                                                  HTTP 400
   */
  @PostMapping("/payment")
  public ResponseEntity<PostPaymentResponse> postPaymentEvent(
      @Valid @RequestBody PostPaymentRequest paymentRequest) {

    return new ResponseEntity<>(paymentGatewayService.processPayment(paymentRequest),
        HttpStatus.OK);
  }
}
