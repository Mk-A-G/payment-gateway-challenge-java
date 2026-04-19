package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostPaymentRequest implements Serializable {

  @NotNull(message = "There is a need for the PAN last 4")
  @Pattern(regexp = "\\d{4}", message = "Must be a 4 digit number")
  @JsonProperty("card_number_last_four")
  private String cardNumberLastFour;

  @Min(1)
  @Max(12)
  @JsonProperty("expiry_month")
  private int expiryMonth;

  @Min(2026)
  @Max(3001)
  @JsonProperty("expiry_year")
  private int expiryYear;

  @NotNull(message = "Currency must be USD, GBP or EUR")
  @Pattern(regexp = "USD|GBP|EUR", message = "Currency must be USD, GBP or EUR")
  private String currency;

  @NotNull
  @Min(1)
  private int amount;

  @NotNull(message = "There is a need for the CVV ")
  @NotBlank(message = "There is a need for the CVV ")
  @Pattern(regexp = "\\d{3}", message = "Must be a 3 digit number")
  private String cvv;

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" + "cardNumberLastFour=" + cardNumberLastFour + ", expiryMonth="
        + expiryMonth + ", expiryYear=" + expiryYear + ", currency='" + currency + '\''
        + ", amount=" + amount + ", cvv=" + cvv + '}';
  }
}

