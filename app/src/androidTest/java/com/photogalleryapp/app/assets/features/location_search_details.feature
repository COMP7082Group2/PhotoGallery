Feature: Location search details
  @smoke
    @e2e
  Scenario Outline: Successful search by locations
    Given I start the application
    When I click search button
    And I click latitude field
    And I enter valid latitude <latitude>
    And I close the keyboard
    And I click longitude field
    And I enter valid longitude <longitude>
    And I close the keyboard
    And I click the go button
    Then I expect to find the picture
    Examples:
      | latitude     | longitude |
      | -123.1207375 | 49.2827   |