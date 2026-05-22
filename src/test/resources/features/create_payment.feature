Feature: Create a payment

  Scenario: Successfully create a valid payment
    Given the payments system is running
    When I submit a payment of "150.00" GBP from "Alice" to "Bob"
    Then the response status should be 201
    And the payment should have status "INITIATED"
    And the payment should have an ID

  Scenario: Reject a payment with a negative amount
    Given the payments system is running
    When I submit a payment of "-50.00" GBP from "Alice" to "Bob"
    Then the response status should be 400

  Scenario: Reject a payment with a missing recipient
    Given the payments system is running
    When I submit a payment of "100.00" GBP from "Alice" to ""
    Then the response status should be 400