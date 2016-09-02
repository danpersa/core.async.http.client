Feature: Get For The Http Client
  As an http client
  I want to do an async get
  So that I can have an async response

  Background:
    Given some sample endpoints
    And an empty world
    And an http client

   Scenario: Successful async get
    When I do a get to "/endpoint-1"
    Then I should get back a map of channels
    And I should get back "200" from the "status-chan"
    And I should get back the UTF-8 string "Hello world and endpoint-1" as byte array from the "body-chan"

  Scenario: Successful async get from a streaming endpoint
    When I do a get to "/streaming-endpoint-1"
    Then I should get back a map of channels
    And I should get back "200" from the "status-chan"
    And I should get back the UTF-8 string "streaming-endpoint-1" as byte array from the "body-chan"
    And I should get back the UTF-8 string " part-0" as byte array from the "body-chan"
    And I should get back the UTF-8 string " part-1" as byte array from the "body-chan"
    And I should get back the UTF-8 string " part-2" as byte array from the "body-chan"
    And I should get back the UTF-8 string "" as byte array from the "body-chan"
    And I should get nil from the "body-chan"

  Scenario: Do an async get to an endpoint which returns 500
    When I do a get to "/error"
    Then I should get back a map of channels
    And I should get back "500" from the "status-chan"
    And I should get back the UTF-8 string "Hello world 500" as byte array from the "body-chan"
    And I should get nil from the "body-chan"

  Scenario: Do an async get to an endpoint which times out
    When I do a get to "/sleep" with a request timeout of 100
    Then I should get an error from the "error-chan"
    And I should get nil from the "status-chan"
    And I should get nil from the "body-chan"