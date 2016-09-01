Feature: Sync Get For The Http Client
  As an http client
  I want to do a sync get
  So that I can have a sync response

  Background:
    Given some sample endpoints
    And an http client

  Scenario: Successful sync get
    When I do a sync get to "/endpoint-1"
    Then I should get the 200 status
    And I should get the body "Hello world and endpoint-1"

  Scenario: Successful sync get from a streaming endpoint
    When I do a sync get to "/streaming-endpoint-1"
    Then I should get the 200 status
    And I should get the body "streaming-endpoint-1 part-0 part-1 part-2"


  Scenario: Call an endpoint which returns 500
    When I do a sync get to "/error"
    Then I should get the 500 status
    And I should get the body "Hello world 500"

  Scenario: Call an endpoint which times out
    When I do a sync get to "/sleep" with a request timeout of 100
    Then I should get an error
