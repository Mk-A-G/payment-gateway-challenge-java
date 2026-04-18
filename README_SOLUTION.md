# Requirements

- A merchant should be able to process a payment through the payment gateway and receive one of the
  following types of response:
  - Authorized - the payment was authorized by the call to the acquiring bank
  - Declined - the payment was declined by the call to the acquiring bank
  - Rejected - No payment could be created as invalid information was supplied to the payment
    gateway and therefore it has rejected the request without calling the acquiring bank
- A merchant should be able to retrieve the details of a previously made payment

# Analysis

Upon first inspection, the first thing I analysed was the code structure and quality.

- There is no field validations to ensure input accuracy and structure.
- There is no security measures implemented
- Logging is minimal
- The storage is a map
- The only tests is the controller therefore the is no testing of the Unit of code
- The response type for the "Proccess Payment" method is incorrect. Only a random UUID is passed not the full object as outlined in the requirements


## Assumptions

1) No need to implement header sanitization as because no values from the header are used or passed to downstream
2) Not implementing any performance enhancing capabilities as the task specifically mentions to not
   overengineer the app
3) No authentication or authorisation required for the purposes of this app


## Design Considerations

1) Controller -> Service -> Repo
2) Error Handling: 
   - An implementation for a response for service unavailable as the current expected responses don't
      effectively cover that scenario
   - Error messaging appropriate for the outcome
   - Field validation and handling
3) Do not plan on using any sort of DB so store the ID. A map should suffice for now.
4) Logging for better observability