package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

@Getter
@Builder
@ToString
public class PostPaymentResponse {
  private UUID id;
  private PaymentStatus status;
  private String cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;
}
