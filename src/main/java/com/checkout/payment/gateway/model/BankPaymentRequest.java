package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
public class BankPaymentRequest {

  @JsonProperty("card_number")
  private String cardNumber;

  @JsonProperty("expiry_date")
  private String expiryDate;

  private String currency;

  private int amount;

  private String cvv;

}
