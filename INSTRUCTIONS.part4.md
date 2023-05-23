# Part 4: from mutable beans to unrepresentable illegal states


## Before people arrive

On flip chart sheets:

* Draw the logical diagram of the scenario
* Draw the state machine and class hierarchy diagrams.

Conceal them for use later.

## Before we start

Ask audience about experience level with Kotlin and Java.

We will live-code the transformation of Java to idiomatic Kotlin.  But this demonstration is intended to be a starting-point for conversations about the topic.  So ask questions as we go.  The digressions *are* the tutorial.

We are expecting you to already know Kotlin.  However, if there are any language features you don't recognise, shout and we'll explain them.

## Explain the domain

The code we are working on implements sign-up for conference sessions.

```plantuml
:Attendee: as a
:Presenter: as p
:Admin: as b

component "Attendee's Phone" as aPhone {
    component [Conference App] as aApp
}
component "Presenter's Phone" as pPhone {
    component [Conference App] as pApp
}
component "Web Browser" as bBrowser
component "Conference Web Service" as webService

a -right-> aApp : "sign-up\ncancel sign-up"
aApp -down-> webService : HTTP

p -left-> pApp : "start session\nlist attendees"
pApp -down-> webService : HTTP

b -right-> bBrowser : "add sessions\nlist attendees"
bBrowser -right-> webService : HTTP

```

Admin user creates sign-up sheets for sessions in an admin app (not covered in this example). Sessions have limited capacity, set when the sign-up sheet is created.
Attendees sign up for sessions via mobile conference app.  Admins can also sign attendees up for sessions via the admin app.
The session presenter starts the session via the mobile conference app.  After that, the sign-up sheet cannot be changed.

The code is simplified for the sake of brevity and clarity:

* It doesn't cover some edge cases.  The techniques we will show apply equally well to those too.
  * PRESENTER NOTE: the example doesn't handle the case of cancelling the signup of an attendee who did not previously sign up, which would leave a full session still full.  If anyone notices, mention that we could either handle this case as an explicit error case, using something like Result4k, or by modifying the state machine to model the idempotency of the cancelSignUp operation.  Either way, we'd use the same techniques we are demonstrating, and so we'll skip these cases unless we have time to implement it at the end of the session.
* It doesn't include authentication, authorisation, monitoring, tracing, etc. to focus on the topic of the exercise. 


## Review the Java code

* show SignupSheet.  Highlight...
  * Beaniness: zero arg constructor, getters and setters, mutable state
  * Queries that execute some business logic (e.g. isFull())
  * Methods that execute business logic (e.g. signUp)
  * Throws exceptions when methods used in the wrong state
  * Defensive copying of mutable collection from getSignUps() accessor
  * Strongly-typed IDs (SessionId, AttendeeId) & Identifier base class -- avoid stringly typed design.

* SignupSheets are collected into a SignupBook
  * Hexagonal architecture
  * Sheets are added to the signup book out-of-band by an admin app, which is not shown in this example.

* SignupHttpHandler implements the HTTP API by which a front-end controls the SignupSheet.
  * Routes on request path and method
  * Supports attendee sign up and  cancellation, starting the session and listing who is signed up.
  * Translates exceptions from the SignupSheet into HTTP error responses

* SessionSignupHttpTests: behaviour is tested at the HTTP API, with fast, in-memory tests.
  * Briefly walk through `can_only_sign_up_to_capacity` 
  * Show the InMemorySignupBook that emulates the semantics of the persistence layer

* SignupServer:
  * Example of a real HTTP server storing signups in memory


Run the tests to show they pass.

Let's convert this to Kotlin.

* Our strategy is to start by converting the domain model and work outwards towards the HTTP layer.


## Adding Kotlin to the project

First we must add Kotlin to the project.

* Use the menu item: Tools > Kotlin > Configure Kotlin in Project
* Reload the Gradle file
* Show changes to the Gradle file
* Change the kotlin.jvmToolchain declaration to match the Java toolchain:

    ~~~
    kotlin {
      jvmToolchain {
        languageVersion.set(java.toolchain.languageVersion)
      }
    }
  ~~~
* Run all the tests – they pass.  We can now use Kotlin in our project!

COMMIT!

ASK: Was that easier than you expected?


## Converting the Bean to Kotlin

Now let's convert the SignupSheet to Kotlin.

* Search for the action "Convert Java file to Kotlin" (hit Shift three times)
  * Copy the key combination onto the whiteboard: we'll be using it again!
* Run the action. It pops up a dialog asking
  > Some code in the rest of your project may require corrections after performing this conversion. Do you want to find such code and correct it too?
  
  Answer "No".  The dialog is misleading.  Clicking "No" does not actually leave code broken, and clicking "Yes" makes changes to the Kotlin and Java we don't want.
  * This is not always the case, and the effect of clicking "Yes" changes with every new version of the Kotlin plugin.  It's always worth trying both options and using the Git diff to see how change has affected your Java code.  However, in large Java code bases, I find it's usually best to click "No" and then add annotations to the Kotlin source to make the Kotlin compiler generate Java bytecode compatible with existing Java. This ensures the remaining Java code remains in conventional Java style while the new Kotlin code follows conventional Kotlin style.
* I won't lie... the converter has produced quite a dog's dinner
  * That's largely because the style of the Java does not match elegant Kotlin style. For the rest of this session, we'll apply Kotlin language features to clean up this code, and then improve it over what is possible in Java
* But first... let's run the tests.
  * They still pass.
  * What does that show us?  Our Java is seamlessly calling into our Kotlin, and our Kotlin seamlessly calling into our Java.  We haven't had to write any interop layer between the Kotlin and the Java.

COMMIT!

Let's tidy up the SignupSheet class before we convert the SignupHttpHandler.  Once we have both classes in Kotlin, we can really start using language features to eliminate some of the nastiness in the SignupSheet class.  But for now, we can at least make the SignupSheet code more concise and expressive, even though it will be mere "Java in Kotlin syntax".

Describe properties ... they are compiled to Java getter/setter methods.  They can have a backing field (e.g. sessionId) or be entirely virtual (e.g. isFull), or you can define the get/set actions to mediate access to the backing field, using the (relatively new) `field` keyword. 

The Java to Kotlin converter hasn't quite kept up with the latest language changes, so we will have to tweak the code to make best use of them...

In the secondary constructor, make sessionId non-nullable.

Run the tests.  They pass. COMMIT!


Make var capacity public, and replace the getCapacity and setCapacity with:

~~~
var capacity = 0
    get
    set(value) {
        check(field == 0) { "you cannot change the capacity after it has been set" }
        field = value
    }
~~~

Run the tests.  They pass.  COMMIT!

Command-click on the `capacity` property to show usages.  The usages include a call to the setter from the Java SignupServer class.  Our change has had no effect on Java code or Kotlin code.  

We do not have uses of `capacity` from Kotlin yet, because apart from the setter call in Java, the capacity is only set by the constructor.  We'll address that presently...

Bring attention to the grey underline of the `get` keyword.  Explain what the grey underlines mean: suggestions for where you can improve code style. Hover over it to show the suggestion.  Option-Enter to apply the suggestion.

Run the tests. They pass. COMMIT!


Now let's look at `signups`.  Move the `getSignups()` method up next to the `signups` property.

* We have a private mutable set that is exposed as Set by a public accessor.
* Command-hover over the Set<AttendeeId> return type declaration.
  * It shows the type is `kotlin.Set`, not `java.util.Set`.
  * In Kotlin, collections are non-mutable by default
  * The getter is making a defensive copy to avoid aliasing bugs.
  * Kotlin has convenience functions for that: Replace `Set.copyOf(signups)` with `signups.toSet()`
  * Mention the naming convention of `toSet` vs `asSet`

Run the tests. They pass. COMMIT!

We want the private property to be a mutable Set, but the public property to be an immutable Set.  We cannot declare the get and set of a property to have different types (at least, at the time of writing).  However, Kotlin provides lots of useful functions for manipulating non-modifiable collections in a functional way.  So, instead, we can replace the immutable reference to a mutable Set with a _mutable_ reference to an immutable Set, and make the setter private (using the same syntax as the code converter generated for isSessionStarted):

* Change the `val signups` to a `var`.  Run the tests to make sure that's not broken anything.
* Command-click on `signups` to pop up usages.  There are two points in this class that mutate the Set.
* Change the declaration of `signups` to: `private var signups = setOf<AttendeeId>()`.  This breaks those two points in the class.
* Navigate to the errors with F2 or by clicking on the error marks in the right gutter.
* Replace the call to the add method with `signups = signups + attendeeId`
* Replace the call to the remove method with `signups = signups - attendeeId`
* Run the tests to make sure everything still works
* Delete the getter and make `var signups` public, with a `private set`.

Run the tests. They pass. COMMIT!

## Review the Kotlin code

The class is now much cleaner... it's an idiomatic Kotlin implementation of Java style "bean" code.

Review other aspects of the code...

* constructor keyword
* check library functions instead of if statements throwing exceptions

ASK: What other differences stand out to the audience?

We can now see the wood for the trees...

* Inappropriate mutation (e.g. sessionId, capacity)
* Throws exceptions if client code uses the object incorrectly.

Wouldn't it be better if the client code could NOT use the object incorrectly?

We can make that happen!  Getting rid of the mutation is the first step on the way, so let's do that first.

The SignupSheet is used in the SignupHttpHandler, so if we will make the SignupSheet immutable, we'll need to change the handler to work with immutable values, rather than mutable beans.  We might as well convert that to Kotlin first...

## Converting the HTTP handler to Kotlin

Convert SignupHttpHandler to Kotlin ... Click "Yes" in the dialog.

Run the tests. They pass. COMMIT!


Review the code of SignupHttpHandler.  The converter has done a pretty good job.

* Explain @JvmField -- show uses of the route constants in the SignupHttpTest to explain how the annotation prevents field references being replaced by calls to getter methods.  Especially useful when you care about the way the call-site looks, such as when you have an "embedded" DSL.
* Explain @Throws ... we don't need them because the exception is declared by the HttpHandler interface, so delete them.

Run the tests. They pass. COMMIT!


The Kotlin code of SignupHttpHandler is still very similar to Java code.  We are not going to change this class very much -- it's "shape" is dictated by the HTTP server library we are using.  However, now that it is in Kotlin we can take advantage of more Kotlin features in the SessionSignup class.  So we will tidy this code up a little, and then get back to SignupSheet...

Use the beige highlights in the right-hand gutter to review the warnings: the IDE is telling us that we should use Kotlin's collection types and functions from the Kotlin standard library.  Let's apply its suggestions using Option-Enter, starting by replacing `List.of` with `listOf`.  Now the class doesn't use Java's List type, and we remove the unused import with "optimise imports" (Control-Option-O).

Run the tests. They pass. COMMIT!

In `handle`, replace `if (xxx == null) { ... }` statements with the elvis operator and `run` scope function.  E.g. 

~~~
doSomething()
   ?: run {
       sendResponse(NOT_FOUND, ... )
   }
~~~

Now to replace use of Java streams with Kotlin's stdlib functions...

In handleSignups, replace the use of streams with Kotlin's map and joinToString extensions.

Run the tests. They pass. COMMIT!


Point out the grey underline on `map` and show the audience the suggestion.

* Apply the suggestion with Option-Enter.
  * The style suggestions are a great way to learn the standard library.  Especially useful when you take on a new Kotlin release with new functions in the stdlib. 
* Option-Enter on `obj` in the lambda, and remove explicit lambda parameter types.  Note the warning "may break code".  Run the tests to confirm that it hasn't.
* Option-Enter on `obj` in the lambda and replace named parameter with `it`, and run the tests.
* The lambda is an ideal candidate to convert to a property reference. Option-Enter on `it.value` -- the option is not available, because the Identifier class is still in Java.  If we have time, we'll come back to the Identifier types and convert them to Kotlin inline classes, but that's not the focus of this session.

Run the tests. They pass. COMMIT!


In `matchRoute`, Option-Enter on the `for` keyword and `Replace with firstOrNull`.  Pretty impressive!

The `matchRoute` function is only used in one place.  Now it's a one-liner, it's not really pulling its weight.
* Inline at the call-site.

Run the tests .  They pass. COMMIT!

Replace the === operators with == in the if statement.  Run the tests.

Now the `if` is highlighted.  Option-Enter and replace `if` with `when`.
 * EXPLAIN: Java's switch statement can only branch on primitive and string types. Kotlin's when can switch on anything.
Option-Enter on the `when` and remove braces from all entries.

Run the tests. They pass. COMMIT!


The HTTP handler is good enough for now... let's return to SignupSheet.


## Converting the bean to an immutable data class

Recall our plan... we will make SignupSheet immutable, and then we will use the type system to make it impossible for client code to call methods when the object is in an inappropriate state.

Remember the refactoring we did for the signups set, in which we replaced an immutable reference to a mutable collection with a mutable reference to an immutable collection?  We'll apply the same strategy to how SignupHttpHandler uses SignupSheet.  

However, we have a chicken-and-egg situation... SignupSheet needs functional operations before we can use the strategy, and we need to have applied the strategy to make SignupSheet functional.  The change feels too big to do in one go.

We need _another_ strategy to break the refactoring into small, safe steps, and that is:

1. Change the SignupSheet so that its API looks functional but also mutates the object -- a so-called "fluent" or "chained" API style.
2. Change clients to use the chained API so that they treat the SignupSheet as if it were immutable
3. Make the SignupSheet immutable

Step 1: make the mutator methods return `this`

* Add `return this` at the end of `sessionStarted`, `signUp` and `cancelSignUp`, and Option-Enter to add the return type to the method signature

Run the tests. They pass. COMMIT!


Step 2: in SignupHttpHandler, replace sequential statements that mutate and then save with a single statement passes the result of the mutator to the `save` method, like:

~~~
book.save(sheet.sessionStarted())
~~~

Run the tests. They pass. COMMIT!


In SignupServer, replace the mutation of the sheet with a call to the constructor and inline the `sheet` variable.

We don't have a test for the server -- it is test code -- but COMMIT! anyway.

We can now delete the no-arg constructor.

* ASIDE: Like most Java code, this example uses Java Bean naming conventions but not actual Java Beans.
* In SignupSheet the no-arg constructor is now unused.  Safe-delete it with Option-Enter.

Run the tests. They pass. COMMIT!

Convert the constructor to a primary constructor by clicking on the declaration and Option-Enter.

Run the tests. They pass. COMMIT!

Make sessionId a non-nullable val declared in primary constructor.

Make capacity a val declared in primary constructor. 

* delete the entire var property including the checks. Those are now enforced by the type system.

Now we can transform the mutator methods into transformations.

First, sessionStarted:

* Move `isSessionStarted` into a val in the primary constructor 
* change sessionStarted() to return a copy of the object passing true to the constructor
* Try running the tests... we've broken Java code.  Java doesn't support default parameters.  But we can make the Kotlin compiler generate overloaded constructors for us by adding the @JvmOverloads annotation to the primary constructor:

  ~~~
  class SignupSheet @JvmOverloads constructor(
      val sessionId: SessionId,
      val capacity: Int,
      ...
  ~~~

Run the tests... they fail!  We also have to update our in-memory simulation of persistence, the InMemorySignupBook.

* pass stored.isSessionStarted() to the constructor and delete the conditional:

    ~~~
    if (stored.isSessionStarted()) {
        loaded.sessionStarted();
    }
    ~~~

Run the tests. They pass. COMMIT!

And now we'll do the same with the set of `signups`:

* Make it a val
* Option-Enter to move to constructor
* Fix the errors by returning a copy of the SignupSheet with the new signup set
* In the InMemorySignupBook, all that code is now unnecessary because SignupSheet is immutable.  Just return the value looked up in the map.

Run the tests. They pass. COMMIT!

We can turn most methods into expression form.

* We cannot do this for signUp because of those checks.  We'll come back to those shortly...

ASIDE: I prefer to use block form for functions with side effects and expression for pure functions.

We can remove duplication by making the code a data class and using the copy method.

* Declare the class as a data class
* Replace all calls to constructor with calls to copy, and remove unnecessary parameters

Run the tests. They pass. COMMIT!

The data class does allow us to make the state of a signup sheet inconsistent, by passing in more signups than the capacity.

* Add a check in the init block:

    ~~~
    init {
        check(signups.size <= capacity) {
            "cannot have more sign-ups than capacity"
        }
    }
    ~~~
* This makes the isFull check in signUp redundant, so delete it.


## Making illegal states unrepresentable

Now... those checks... it would be better to prevent client code from using the SignupSheet incorrectly than to throw an exception after they have used it incorrectly.  In FP circles this is sometimes referred to as "making illegal states unrepresentable". 

The SignupSheet class implements a state machine:

IF TIME: we need better names... ask the audience for suggestions.

~~~plantuml

state Open {
    state choice <<choice>>
    state closed <<exitPoint>>
    state open <<entryPoint>>
    
    open -down-> Available
    Available -down-> Available : cancelSignUp(a)
    Available -right-> choice : signUp(a)
    choice -right-> Full : [#signups = capacity]
    choice -up-> Available : [#signups < capacity]
    Full -left-> Available : cancelSignUp(a)
    
    Available -> closed : sessionStarted()
    Full -> closed : sessionStarted()
}

[*] -down-> open
closed -> Closed
~~~

REVEAL: the state diagram drawn on the flip-chart...

* The signUp operation only makes sense in the Available sub-state of Open.

* The cancelSignUp operation only makes sense in the Open state.

* The sessionStarted operation only makes sense in the Open state. 

We can express this in Kotlin with a _sealed type hierarchy_...

REVEAL: the type hierarchy drawn on the flip-chart.

~~~plantuml
hide empty members
hide circle

class SignupSheet <<sealed>>
class Open <<sealed>> extends SignupSheet {
    sessionStarted(): Closed
    cancelSignUp(a): Available
}

class Available extends Open {
    signUp(a): SignupSheet
}

class Full extends Open

class Closed extends SignupSheet
~~~


We'll introduce this state by state, starting with Open vs Started, replacing predicates of the properties of the class with subtype relationships.

Unfortunately IntelliJ doesn't have any automated refactorings to split a class into a sealed hierarchy, so we'll have to do it the old-fashioned way... by hand ... like C++ programmers...

### Open/Closed states

* Extract an abstract base class from SignupSheet
  * NOTE: IntelliJ seems to have lost the ability to rename a class and extract an interface with the original name.  So, we'll have to extract the base class with a temporary name and then rename class and interface to what we want.
  * call it anything, we're about to rename it.  SignupSheetBase, for example. 
  * Pull up sessionId, capacity & signups as abstract members and isSignedUp as a concrete member.
  * This refactoring doesn't work 100% for Kotlin, so fix the errors in the interface by hand. 

* Change the name of the subclass by hand (not a rename refactor) to Open, and then use a rename refactoring to rename the base class to SignupSheet.
* Repeatedly run all the tests to locate all the compilation errors... 
  * In SignupHttpHandler, there are calls to methods of the Open class that are not defined on the SignupSheet class.
    * wrap the try/catch blocks in `when(sheet) { is Open -> try { ... } }` to get things compiling again. E.g.

      ~~~
      when (sheet) {
          is Open ->
              try {
                  book.save(sheet.signUp(attendeeId))
                  sendResponse(exchange, OK, "subscribed")
              } catch (e: IllegalStateException) {
                  sendResponse(exchange, CONFLICT, e.message)
              }
          }
      }
      ~~~
      
  * In SessionSignupHttpTests and SignupServer we need to create Open instead of SessionSignup.
    * If we convert all call sites to Kotlin first, there are tricks we can use to do this safely without manual edits.  IntelliJ doesn't yet have a "Replace constructor with factory method" refactoring for Kotlin classes.  However, there are so few places that create the new Availability objects it is not worth introducing a factory method.  We'll fix it up by hand...
    * Fix it up by hand.
      * Easiest way is to select "new SignupSheet", then Command-R to replace all instances with "new Open"
      
* Run the tests.  They should all pass.
* Change the base class from "abstract" to "sealed".

Run the tests. They pass. COMMIT!

Now we can add the Closed subclass:

* NOTE: do not use the "Implement sealed class" action... it does not give the option to create the class in the same file. Instead... 
* Define a new `data class Closed : SignupSheet()` in the same file
* The new class is highlighted with an error underline. Option-Enter on the highlighted error, choose "Implement as constructor parameters", ensure sessionId, capacity, and signups are selected in the pop-up (default behaviour), and perform the action.
* Option-Enter on the highlighted error again, choose "Implement members", select all the remaining members

We've broken our HTTP handler, so before we use the Closed class to implement our state machine, let's get it compiling again.

* Add when clauses for Closed that just call TODO(), by Option-Enter-ing on the errors and selecting "Add remaining branches"

Run the tests to verify that we have not broken anything... we are not actually using the Closed class yet.

Now make Open.sessionStarted() return an instance of Closed:

~~~
fun sessionStarted() =
    Closed(sessionId, capacity, signups)
~~~

Run the tests: there are failures because of the TODO() calls:

* in handleSignup, replace TODO calls by sending a CONFLICT status with an error message (e.g. "session started") as the body text.
* in handleStarted:
  * GET: replace with returning `sheet is Closed`
  * POST: there is nothing to do if the session is already started, replace the TODO() with an empty branch and a comment like "// nothing to do" and move the call to sendResponse after the `when` block.

Run the tests. They pass. COMMIT!

Look for uses of isSessionStarted. The only calls are accessors in the checks.  Therefore, the value never changes, and is always false.  The checks are dead code, because we have replaced the use of the boolean property with subtyping.

* Delete the check statements
* Safe-Delete the isSessionStarted constructor parameter

Run the tests. They pass. COMMIT!

Review the class... now we have methods that return the abstract SessionSignup type.  We can make the code express the state transitions explicitly in the type system be declaring the methods to return the concrete type (or letting Kotlin infer the result type).

* ASIDE: I prefer to explicitly declare the result type I want.
* Declare the result of sessionStarted() as Closed, and of signUp & cancelSignUp as Open

Run the tests. They pass. COMMIT!

### Available/Full states

We still have the try/catch blocks because the SignupSheet throws IllegalStateException if you call sign up when the session is full.  We can represent that with types in the same way...

Rename Open to Available

Run all the tests.  They should still pass.

Extract an abstract superclass Open, pulling up sessionStarted and cancelSignUp as concrete. (Ignore the members highlighted in red in the dialog -- they will be inherited from the SignupSheet base class).

Make Open a sealed class.  This will get rid of any compilation errors.

Run all the tests.  They should still pass.

Add a new subclass, Full, derived from Open, like this:

~~~
data class Full : Open()
~~~

* It will be underlined with a red error highlight. 
* Option-Enter on the error, select "Implement as constructor parameters", and select sessionId and signups in the dialog
* The class will still be underlined with a red error highlight because `capacity` has not been implemented yet
* Option-Enter on the error, select "Implement members", and select Ok
* Implement `capacity` to evaluate to `signups.size`
* The end result should therefore be:

    ~~~
    data class Full(
        override val sessionId: SessionId,
        override val signups: Set<AttendeeId>
    ) : Open() {
        override val capacity: Int
            get() = signups.size
    }
    ~~~

Run all the tests.  Now SignupHttpHandler won't compile because the Full case is not handled.

Make all the `when` expressions exhaustive:

* in handleSignup for POST, Option-Enter on the `when` and choose "Add remaining branches"
* in handleSignup for DELETE and handleStarted, change when condition from `is Available` to `is Open`


Change Available::signUp to return Available or Full, depending on whether the number of signups reaches capacity:

* extract `signups + attendeeId` as a variable, newSignups
* Change result to return Full when newSignups.size == capacity:

    ~~~
    return when (newSignups.size) {
        capacity -> Full(sessionId, newSignups)
        else -> copy(signups = newSignups)
    }
    ~~~

Run the tests.  They fail.

Make them pass by:

* Implementing the `is Full` condition as:

  ~~~
  is Full -> {
      sendResponse(exchange, CONFLICT, "session full")
  }
  ~~~

Run the tests. They pass. COMMIT!

Review the subclasses of SignupSheet.  The classes no longer check that methods are called in the right state.  The only remaining check, in the init block, defines a class invariant that the internal implementation maintains.  We can remove the try/catch in our HTTP handler!

* Unwrap the try/catch blocks in the SignupHttpHandler (add braces to when clause with Option-Enter if necessary)


## If time: Converting the methods to extensions

If we have time, convert methods to extensions (Option-Enter on the methods).

Change the result types to the most specific possible. 

Gather the types and functions into two separate groups.

Fold away the function bodies. Ta-da!  The function signatures describe the state machine!


## If time: Converting identifiers to value classes

Convert Java to Kotlin, remove the inheritance and edit to be a value class. Then inline the `of` method. It's not required.


## Wrap up

Review the code of SignupSheet and SignupHttpHandler

What have we done?

* Converted Java code to Kotlin _incrementally_, ensuring the project is always working and gradually making use of more Kotlin features as Kotlin spreads through our codebase.
* Used IntelliJ's automatic refactorings, corrections and code intentions wherever possible -- we seldom needed to edit the code _as text_. 
* Refactored a mutable, object-oriented domain model to an immutable, algebraic data type and operations on the data type.
  * Pushed mutation outward, to the edge of our system
* Replaced runtime tests throwing exceptions for invalid method calls, with type safety: it is impossible to call methods in the wrong state because those operations do not exist in those states
  * Pushed error checking outwards, to the edge of the system, where the system has the most context to handle the error
* IF TIME: Fixed a subtle bug in our original code... did anyone spot it?
  * Show original version
  * Answer: both HttpExchange and SignupSheet can throw IllegalStateException, and the error handling does not distinguish between the two situations.  In our final version, we clearly distinguish between expected states in our domain and programming errors.
