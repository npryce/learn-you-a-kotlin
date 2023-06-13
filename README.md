Presentation: Refactoring Java to Kotlin, OO to FP
==================================================

This branch contains the sequence of changes committed during the presentation.

We...

1. Converted Java code to Kotlin
   * and tidied it up to concise “Java in Kotlin clothing” 
2. Converted the business logic to a functional domain model
   * and the software architecture to “functional core / imperative shell” 
3. Represented the state machine with a sealed class hierarchy & functions
    * and made illegal states unrepresentable

... without breaking the application for more than a few seconds at a time.
