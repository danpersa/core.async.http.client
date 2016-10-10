Feature: Post For The Http Client
  As an http client
  I want to do an async post
  So that I can have an async response

  Background:
    Given some sample endpoints
    And an empty world
    And an http client

  Scenario: Successful async post
    When I do a post with "Hello World" to "/echo"
    Then I should get back a map of channels
    And I should get back "200" from the "status" chan
    And I should get back the UTF-8 string "Hello World" as byte array from the "body" chan
