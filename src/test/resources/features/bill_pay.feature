@cucumber @billpay
Feature: Pay a bill from my bank account
  As a bank customer
  I want to pay a named payee from my account
  So that I can settle my bills.

  Background:
    Given I am a newly registered bank customer
    And I have a paying account with at least 25 dollars available

  @positive @smoke
  Scenario: Pay a bill with complete and matching payee details
    When I pay 25 dollars to "Demo Electricity" using valid payee details
    Then I should receive confirmation of the 25 dollar payment to "Demo Electricity"
    And my paying account balance should decrease by 25 dollars

  @negative
  Scenario Outline: Incomplete or inconsistent payment details prevent payment
    When I submit a bill payment with "<problem>"
    Then I should see the bill payment validation message "<message>"
    And my paying account balance should remain unchanged

    Examples:
      | problem                         | message                              |
      | a missing payee name            | Payee name is required.              |
      | mismatched payee account numbers | The account numbers do not match.   |
      | a missing payment amount        | The amount cannot be empty.          |
      | a non-numeric payment amount    | Please enter a valid amount.         |
