Feature: Web & SQL interactions
 
Scenario: Get on some resource and test the result from database
  Given an SQL table CUSTOMER containing
  | ID | FIRST_NAME | LAST_NAME  |
  | 1  | John       | WHO        |
  | 2  | Scarlett   | Johansson  |
  When an HTTP GET request is made on /list_users resource
  Then the HTTP response code should be OK
  Then the HTTP response body should be ["John WHO","Scarlett Johansson"]

Scenario: Push data into DB and assert through resource call the result of select
  When an HTTP GET request is made on /create_user?firstName=Jessica&lastName=Alba resource
  When an HTTP GET request is made on /list_users resource
  Then the HTTP response body should be ["Jessica Alba"]
