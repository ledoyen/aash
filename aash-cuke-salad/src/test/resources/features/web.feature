Feature: Web interactions
 
Scenario: Get on some resource and test the result
  When a GET request is made on /test resource
  Then the response code should be OK
  Then the response body should be 4343

Scenario Outline: Get on some resource and test the result
  When a GET request is made on /says/<message> resource
  Then the response code should be OK
  Then the response body should be <response>

Examples:
    | message         | response    |
    | say_HeLLo       | HeLLo       |
    | say_Something45 | Something45 |
