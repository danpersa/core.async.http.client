Feature: Sample endpoints
  As an http client
  I want to have some sample endpoints
  So that I can call during the acceptance tests

  Scenario: Starts the sample endpoints
    Given some sample endpoints
    And an empty world
    And an http client
    When I do a sync get to "/endpoint-1"
    Then I should get the body "Hello world and endpoint-1"
