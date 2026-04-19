package com.checkout.payment.gateway;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

  PostPaymentRequest paymentRequest;
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    paymentRequest = new PostPaymentRequest();
    paymentRequest.setAmount(10);
    paymentRequest.setCurrency("USD");
    paymentRequest.setExpiryMonth(12);
    paymentRequest.setExpiryYear(2026);
    paymentRequest.setCardNumber("4321432143214321");
    paymentRequest.setCvv("123");

  }

  @Test
  void postPaymentEvent() throws Exception {

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(
            paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4)))
        .andExpect(jsonPath("$.expiryMonth").value(paymentRequest.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentRequest.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentRequest.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentRequest.getAmount()));
  }

  @Test
  void postPaymentEventButSystemIsDown() throws Exception {
    paymentRequest.setCardNumber("4321432143214320");

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.message").value("Your bank is down"));
  }


}
