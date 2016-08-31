Feature: Fragments
  As a mosaic shop
  I want to have some fragments
  So that I can provide features for the customers

  Scenario: Start the fragments
    Given some fragments
    When I check if the fragments are started
    Then they should be started
