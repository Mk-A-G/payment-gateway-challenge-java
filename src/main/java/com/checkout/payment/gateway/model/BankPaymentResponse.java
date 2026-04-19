package com.checkout.payment.gateway.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankPaymentResponse {

  boolean authorized;

  @JsonProperty("authorization_code")
  String authorizationCode;

}
