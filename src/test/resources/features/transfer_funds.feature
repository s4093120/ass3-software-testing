@cucumber @transfer
Feature: Transfer funds between my accounts
  As a bank customer
  I want to transfer money between my accounts
  So that I can manage my funds.

  Background:
    Given I am a newly registered bank customer
    And I have two different accounts available for a transfer

  @positive @smoke @first
  Scenario: Transfer a valid amount between two accounts
    Given my source account has at least 10 dollars available
    When I transfer 10 dollars from my source account to my destination account
    Then I should receive confirmation of the 10 dollar transfer
    And my source balance should decrease by 10 dollars
    And my destination balance should increase by 10 dollars

  @negative
  Scenario Outline: An invalid amount must not move money
    When I attempt a transfer using "<amount>" as the amount
    Then the transfer should be unsuccessful with an error message
    And both account balances should remain unchanged

    Examples:
      | amount |
      |        |
      | abc    |

  @positive
  Scenario: Transfer the entire available balance
    Given my source account has at least 1 dollar available
    When I transfer all my available funds from my source account to my destination account
    Then I should receive confirmation of the whole transfer
    And my source balance should be zero
    And my destination balance should increase by the full transfer amount
