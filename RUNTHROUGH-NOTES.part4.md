# Notes

1. Need an example of production code that would use a SessionSignup, not just the tests.  E.g. an HTTP handler, using a repository, to implement sign-up API.

2. The SessionSignup should have a SessionId to be more realistic

3. Remove some business rules:
   * that a user can sign up when session is full if they are already signed up
   * that a user can sign up after the session has started if they are already signed up

4. Start with the test creating a SessionSignup in each test method


