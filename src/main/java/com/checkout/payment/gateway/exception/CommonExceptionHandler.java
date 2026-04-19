package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  /**
   * Handles {@link EventProcessingException}, thrown when ther is no transaction found.
   *
   * @param ex the exception containing binding results and the rejected target object
   * @return HTTP 404 with a {@link ErrorResponse}
   */
  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Page not found"),
        HttpStatus.NOT_FOUND);
  }

  /**
   * Handles {@link BankingSystemUnavailableException}, thrown when the banking system is down.
   *
   * @param ex the exception containing binding results and the rejected target object
   * @return HTTP 503 with a {@link ErrorResponse}
   */
  @ExceptionHandler(BankingSystemUnavailableException.class)

  public ResponseEntity<ErrorResponse> handleException(BankingSystemUnavailableException ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Your bank is down"),
        HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   * Handles {@link MethodArgumentNotValidException}, thrown when a request body
   * fails validation constraints.
   * As per spec,  <a href="https://github.com/cko-recruitment/#requirements">...</a> The merchant
   * is expected receive  the rejected status
   * @param ex the exception containing binding results and the rejected target object
   * @return HTTP 400 with a {@link PostPaymentResponse} with status {@code REJECTED}
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<PostPaymentResponse> handleException(
      MethodArgumentNotValidException ex) {

    LOG.error("Exception happened", ex);

    PostPaymentRequest requestBody = (PostPaymentRequest) ex.getBindingResult().getTarget();

    assert requestBody != null;
    return new ResponseEntity<>(PostPaymentResponse.builder()
        .status(PaymentStatus.REJECTED)
        .cardNumberLastFour(
            requestBody.getCardNumber().substring(requestBody.getCardNumber().length() - 4))
        .expiryMonth(requestBody.getExpiryMonth())
        .expiryYear(requestBody.getExpiryYear())
        .currency(requestBody.getCurrency())
        .amount(requestBody.getAmount())
        .build(),
        HttpStatus.BAD_REQUEST);
  }


}
