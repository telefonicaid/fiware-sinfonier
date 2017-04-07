# -*- coding: utf-8 -*-

Feature: Login using LST


  Scenario: Change the language page
    Given I navigate to the "[CONF:services.sinfonier.url]" service url for "Login" web page
    When   I click on "button_language" button
    And   I click on "button_language_english" button
    And   I wait 2 seconds
    Then  the text of the element "text_title" contains "[LANG:string_login.form_elements.title]"


  Scenario Outline: Login using invalid credentials.
    Given I navigate to the "[CONF:services.sinfonier.url]" service url for "Login" web page
    When  I fill in "username" field from "Login" page with "<username>"
    And   I fill in "password" field from "Login" page with "<password>"
    And   I click on "login" button
    And   I wait 2 seconds
    Then  an invalid credential error is shown with message "[LANG:login.error_message.invalid_credential]"


    Examples: Credentials - <description>
      | username                             | password                                 | description        |
      | [EMPTY]                              | [EMPTY]                                  | EMPTY BOTH         |
      | [CONF:services.sinfonier_admin.user] | [EMPTY]                                  | EMPTY PASS         |
      | [EMPTY]                              | [CONF:services.sinfonier_admin.password] | EMPTY USER         |
      | [CONF:services.sinfonier_admin.user] | CONTRASENYA_INCORRECTA                   | PASS INCORRECT     |
      | !"$%&/([=?=}(/&'                     | <\$%&/(?*}{;:_>                          | SPECIAL CHARACTERS |


