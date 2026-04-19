package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number_last_four")
  private String cardNumberLastFour;
  @JsonProperty("expiry_month")
  private int expiryMonth;
  @JsonProperty("expiry_year")
  private int expiryYear;
  private String currency;
  private int amount;
  private String cvv;

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
