@cucumber @loan
Feature: Request a loan
  As a bank customer
  I want a clear decision on my loan request
  So that I can plan my borrowing.

  Background:
    Given I am a newly registered bank customer

  @positive @smoke
  Scenario: An eligible request creates a new loan account
    Given my account has at least 50 dollars available for a down payment
    When I request a loan of 100 dollars with a down payment of 50 dollars
    Then my loan request should be approved
    And a new loan account should contain 100 dollars
    And my funding account balance should decrease by 50 dollars

  @negative
  Scenario: A down payment exceeding my available funds is rejected
    When I request a loan with a down payment greater than all my available funds
    Then my loan request should be denied with an explanation
    And no new loan account should be created
    And my funding account balance should remain unchanged
