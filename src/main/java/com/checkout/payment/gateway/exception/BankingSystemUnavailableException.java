package com.checkout.payment.gateway.exception;

public class BankingSystemUnavailableException extends RuntimeException {

  public BankingSystemUnavailableException(String message) {
    super(message);
  }
}
