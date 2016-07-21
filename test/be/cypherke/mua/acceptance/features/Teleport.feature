Feature: Teleporting players

  Background:
    Given a player 'user'

  Scenario: Add a teleport location
    When the player adds a teleport location
    Then the location is added to the teleport list

  Scenario: Delete a teleport location
    And the player has a teleport location 'home'
    When the player deletes the teleport location 'home'
    Then the location is no longer present