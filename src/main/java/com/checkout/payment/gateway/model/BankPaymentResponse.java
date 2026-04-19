package com.checkout.payment.gateway.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BankPaymentResponse {

  boolean authorized;

  @JsonProperty("authorization_code")
  String authorizationCode;

}
