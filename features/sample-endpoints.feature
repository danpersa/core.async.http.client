Feature: Sample endpoints
  As an http client
  I want to have some sample endpoints
  So that I can call during the acceptance tests

  Scenario: Starts the sample endpoints
    Given some sample endpoints
    When I check if the sample endpoints are started
    Then they should be started
