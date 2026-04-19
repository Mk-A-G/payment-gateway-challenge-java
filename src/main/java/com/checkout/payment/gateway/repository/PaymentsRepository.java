package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
/**
 * Mock repository  for handling payment gateway persistence.
 *
 * <p> uses a map to store payment information and authorization results.
 *
 */
@Repository
public class PaymentsRepository {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentsRepository.class);

  private final HashMap<UUID, PostPaymentResponse> payments = new HashMap<>();

  /**
   * Persists a payment response record in the in-memory store.
   *
   * <p>The payment is keyed by its {@link PostPaymentResponse#getId() ID}.
   * If a payment with the same ID already exists it will be overwritten.
   *
   * @param payment the payment response to store
   */
  public void add(PostPaymentResponse payment) {
    LOG.info("Saving payment in repository with ID: {}", payment.getId());
    payments.put(payment.getId(), payment);
    LOG.info("Payment successfully saved");
  }

  /**
   * Retrieves a transaction record by its unique identifier.
   *
   * @param id the UUID of the payment to retrieve; must not be null
   * @return an {@link Optional} containing the {@link PostPaymentResponse} if found,
   *         or {@link Optional#empty()} if no payment exists for the given ID
   */
  public Optional<PostPaymentResponse> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}
