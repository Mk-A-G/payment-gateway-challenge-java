package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.downstream.BankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
/**
 * Service and orchestrator for the PaymentGatewayController.
 *
 */
@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;

  private final BankClient bankClient;


  public PaymentGatewayService(PaymentsRepository paymentsRepository, BankClient bankClient) {
    this.paymentsRepository = paymentsRepository;
    this.bankClient = bankClient;
  }

  /**
   * Gets a payment response record from the repository (the HashMap).
   *
   *
   * @param id the payment response in the store
   * @throws EventProcessingException when the repo returns a null object
   */
  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.info("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  /**
   * Sends a payment request record to the bank for authorization andn the uses
   * the repository to persist the data.
   *
   * <p>The full PAN is never persisted — only the last four digits are retained
   * in the stored response.
   *
   * @param paymentRequest the validated payment request containing card, currency,
   *                       and amount details; must not be null
   * @return the {@link PostPaymentResponse} containing the generated payment ID,
   *         authorisation status, and masked card details
   * @throws com.checkout.payment.gateway.exception.BankingSystemUnavailableException
   *         if the downstream bank is unreachable or returns a server error
   */
  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    LOG.info("Requesting payment Auth to payment with pan last 4 {}",
        extractLastFourDigitsFromPan(paymentRequest.getCardNumber()));
    BankPaymentResponse response = callBankForAuth(paymentRequest);
    LOG.info("Payment Auth Response: {}", response);
    PostPaymentResponse postPaymentResponse = constructPostPaymentResponse(paymentRequest, response);

    paymentsRepository.add(postPaymentResponse);

    return postPaymentResponse;

  }

  /**
   * Extracts the last four digits from a card number (PAN).
   *
   * @param cardNumber the full card number; must not be null and must be at least 4 characters
   * @return a 4-character string containing the last four digits of the card number
   */
  private String extractLastFourDigitsFromPan(String cardNumber) {
    return cardNumber.substring(cardNumber.length() - 4);
  }

  /**
   * Constructs and sends a {@link BankPaymentRequest} to the downstream bank.
   *
   * @param paymentRequest the original payment request
   * @return the {@link BankPaymentResponse} returned by the bank
   */
  private BankPaymentResponse callBankForAuth(PostPaymentRequest paymentRequest) {
    return bankClient.processPayment(BankPaymentRequest.builder()
        .cardNumber(paymentRequest.getCardNumber())
        .expiryDate(paymentRequest.getExpiryDate())
        .currency(paymentRequest.getCurrency())
        .amount(paymentRequest.getAmount())
        .cvv(paymentRequest.getCvv())
        .build());
  }

  /**
   * Constructs a {@link PostPaymentResponse} from the original request and the
   * bank's authorisation response.
   *
   * <p>A new UUID is generated for the payment ID. The authorisation status is
   * derived from {@link BankPaymentResponse#isAuthorized()}.
   *
   * @param paymentRequest the original payment request
   * @param response       the authorisation response from the bank
   * @return a fully populated {@link PostPaymentResponse}
   */
  private PostPaymentResponse constructPostPaymentResponse(PostPaymentRequest paymentRequest,
      BankPaymentResponse response) {
    return PostPaymentResponse.builder()
        .id(UUID.randomUUID())
        .amount(paymentRequest.getAmount())
        .expiryYear(paymentRequest.getExpiryYear())
        .expiryMonth(paymentRequest.getExpiryMonth())
        .currency(paymentRequest.getCurrency())
        .cardNumberLastFour(extractLastFourDigitsFromPan(paymentRequest.getCardNumber()))
        .status(response.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED)
        .build();
  }

}
