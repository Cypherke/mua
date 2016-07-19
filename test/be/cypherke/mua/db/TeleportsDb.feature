Feature: Teleport database

  Scenario: Add a new teleport location
    Given an empty teleport database
    When I add a new teleport location with the name 'test'
    Then the new teleport location 'test' is added to the database


  Scenario: Remove a teleport location
    Given the player 'user' has only one teleport location named 'test'
    When I remove the teleport location 'test' for the player 'user'
    Then the player 'users' has no more teleport locations
