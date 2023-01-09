package com.amigoscode.customer;

import com.amigoscode.clients.fraud.FraudCheckResponse;
import com.amigoscode.clients.fraud.FraudClient;
import com.amigoscode.clients.notification.NotificationClient;
import com.amigoscode.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {
    private final RestTemplate restTemplate;
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build(); // Builder Pattern

        // todo: check if email valid
        // todo: check if email not taken

        // todo: store customer in db
        customerRepository.saveAndFlush(customer); // saveAndFlush used to get the customer id

        // todo: check if fraudster
        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        // todo: send notification
        notificationClient.sendNotification(
              new NotificationRequest(
                      customer.getId(),
                      customer.getEmail(),
                      String.format("Hi %s's, welcome to Amigoscode....",
                              customer.getFirstName())
              )
        );
    }
}
