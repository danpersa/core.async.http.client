Feature: Sync Post For The Http Client
  As an http client
  I want to do a sync post
  So that I can have a sync response

  Background:
    Given some sample endpoints
    And an empty world
    And an http client

  Scenario: Successful post
    When I do a sync post with "Hello World" to "/echo"
    Then I should get the 200 status
    And I should get the body "Hello World"
