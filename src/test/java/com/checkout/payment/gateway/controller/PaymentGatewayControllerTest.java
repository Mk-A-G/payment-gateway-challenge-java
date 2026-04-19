package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  PaymentsRepository paymentsRepository;
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
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse paymentResponse = PostPaymentResponse.builder()
        .id(UUID.randomUUID())
        .amount(10)
        .currency("USD")
        .status(PaymentStatus.AUTHORIZED)
        .expiryMonth(12)
        .expiryYear(2026)
        .cardNumberLastFour("4321").build();

    paymentsRepository.add(paymentResponse);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + paymentResponse.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(paymentResponse.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(paymentResponse.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(paymentResponse.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentResponse.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentResponse.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentResponse.getAmount()));
  }

  @Test
  void postPaymentEvent() throws Exception {

    paymentRequest.setAmount(10);
    paymentRequest.setCurrency("USD");
    paymentRequest.setExpiryMonth(12);
    paymentRequest.setExpiryYear(2026);
    paymentRequest.setCardNumberLastFour("4321");
    paymentRequest.setCvv("123");

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(paymentRequest.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(paymentRequest.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentRequest.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentRequest.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentRequest.getAmount()));
  }

  @Test
  void postPaymentEventThrowsConstraintViolationErrorsWhenFieldsAreNull() throws Exception {

    paymentRequest = new PostPaymentRequest();

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "GHS"})
  void postPaymentBlankCurrencyReturns400(String currency) throws Exception {

    paymentRequest.setCurrency(currency);

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "GHS", "2134523457"})
  void postPaymentInvalidCardNumberReturns400(String lastFour) throws Exception {

    paymentRequest.setCardNumberLastFour(lastFour);

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void postPaymentZeroAmountReturns400() throws Exception {

    paymentRequest.setAmount(0);

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 24})
  void postPaymentWrongMonthFormatReturns400(int month) throws Exception {

    paymentRequest.setExpiryMonth(month);

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 24, 3002})
  void postPaymentWrongYearFormatReturns400(int year) throws Exception {

    paymentRequest.setExpiryYear(year);

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "24", "3002"})
  void postPaymentWrongCVVFormatReturns400(String cVV) throws Exception {

    paymentRequest.setCvv(cVV);

    mvc.perform(MockMvcRequestBuilders.post("/payment").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }
}
