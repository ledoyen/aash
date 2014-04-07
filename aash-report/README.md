# Aash Report

A tool to schedule SQL requests and results by email.

## Scope

A Scope is made of 3 parts :
 * Request declarations
 * Mail definitions

### Request declarations

This is where you define as many requests as you want, usually in the same _functional_ scope.

Requests can have to type of result :
 * Single value
 * Grid

### Mail definitions

This is where you define mails, using the previously declared request results.

Mail subject and body are handled by a velocity template.

