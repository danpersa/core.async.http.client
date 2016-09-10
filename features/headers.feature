Feature: Add request headers
  As an http client
  I want to be able to specify headers
  So that I can do a nice request

  Background:
    Given some sample endpoints
    And an empty world
    And an http client

  Scenario: Successfully specify headers
    Given I prepare the header with name "x-header-1" and value "value-1"
    And I prepare the header with name "x-header-2" and value "value-2"
    And I prepare the header with name "x-header-3" and values "value-3" and "value-4"
    When I do a sync get to "/x-headers"
    Then I should get the body "x-header-1: value-1, x-header-2: value-2, x-header-3: value-3,value-4"
