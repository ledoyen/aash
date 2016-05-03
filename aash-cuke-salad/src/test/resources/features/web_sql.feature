Feature: Web & SQL interactions
 
Scenario: Get on some resource and test the result from database
  Given a table CUSTOMER containing
  | ID | FIRST_NAME | LAST_NAME  |
  | 1  | John       | WHO        |
  | 2  | Scarlett   | Johansson  |
  When a GET request is made on /list_users resource
  Then the response code should be OK
  Then the response body should be ["John WHO","Scarlett Johansson"]

Scenario: Push data into DB and assert through resource call the result of select
  When a GET request is made on /create_user?firstName=Jessica&lastName=Alba resource
  When a GET request is made on /list_users resource
  Then the response body should be ["Jessica Alba"]
