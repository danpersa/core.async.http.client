Feature: Sync Get For The Http Client
  As an http client
  I want to do a sync get
  So that I can have a sync response

  Background:
    Given some sample endpoints

  Scenario: Successful sync get
    Given some sample endpoints
    When I do a sync get to "/endpoint-1"
    Then I should get the 200 status
    And I should get the body "Hello world and endpoint-1"

  Scenario: Successful sync get from a streaming endpoint
    Given some sample endpoints
    When I do a sync get to "/streaming-endpoint-1"
    Then I should get the 200 status
    And I should get the body "streaming-endpoint-1 part-0 part-1 part-2"


  Scenario: Called an endpoint which returns 500
    Given some sample endpoints
    When I do a sync get to "/error"
    Then I should get the 500 status
    And I should get the body "Hello world 500"
