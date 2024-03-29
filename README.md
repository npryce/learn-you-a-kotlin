Exercise for the tutorial "Refactoring to Kotlin"
=================================================

Aim of the session is to introduce Kotlin by converting Java code.

First create a new branch. Check in after each change.  This lets you
easily show how auto-converting code to Kotlin affects how its API
looks when used from Java

Suggested progress

* Part 0: Compiling Kotlin
  * Open project in IntelliJ, build on command line, run tests in IntelliJ
  * Tools/Kotlin/Configure Kotlin in Project
  * Look at the changes to build.gradle
  * Build and test
  * Tools/Kotlin/Configure Kotlin Plugin Updates - use New Java To Kotlin Converter

* Part 1: Class syntax and data classes
  * Presenter
    * note that this an immutable value class with a public final field 
    * convert to Kotlin, accept change other files.
    * try to run tests - see compilation failure, talk about converter 
    * talk about properties v fields, fix JsonFormat.java (Alt-Enter)
    * talk through the bits of the Kotlin class - ctor, property, methods
    * run tests
    * remove equals, hashcode, to string - show tests fail
    * make a data class - show tests now pass
    * remove the unneeded class body
    * check in
  * Session
    * note that this is an immutable value class with public fields, one of which is nullable, and it defensively copies the presenters
    * also that we have 2 constructors - one a convenience vararg
    * convert to Kotlin
    * Note subtitle is a `String?` - talk about nullability
    * Note primary v secondary constructor, observe primary ctor invocation
    * Note we can have a free property - presenters, initialised in class body
    * Talk about init block, but then remove it
    * show tests pass, remove equals etc - show tests fail
    * convert to a data class
    * can't have a val outside ctor, remove it
    * No need to wrap List in unmodifiableList: discuss List/MutableList split, show List defn
    * Observe spread operator in constructor, remove it and replace with presenters.toList() - discuss asList()
    * Remove empty ctor body
    * Convert withXxx methods to single expression  - note lack of new
    * Convert withXxx methods to invoke .copy (do via add argument names and talk about argument names)
    * run the tests, check diffs, talk about diffs, checkin
  * SessionTests  
    * convert to Kotlin
    * run tests
    * talk about "internal"
    * talk about "var" vs "val"
    * talk about lack of a "new" keyword -- classes look like, and can be used as, functions
    * talk about listOf vs Arrays.asList -- Kotlin stdlib has lots of useful collection methods
    * talk about lack of return type when Unit
    * Now all of our Session clients are Kotlin we can inline the 'copy's, except for 
      withPresenters, which we can make vararg
    * Now the copy invocations don't need testing
    * Move withPresenters methods out of class into extension ... much nicer in Kotlin, yeah?
    * Explain extension functions in more detail ... syntactic sugar for static methods
    * Move withPresenters into SessionTests where it is used to illustrate convenience extensions
    * Talk about top-level functions
    * Rename test to `illustrate convenience extension methods` and talk about names
    * run the tests, check diffs, talk about diffs, checkin
  * Slots  
    * Convert Slots.  It's all Kotlin!!! That was easy!
    * run the tests, check diffs, talk about diffs, checkin

* Part 2: Null and nullability
  * Look at Sessions - a bunch of static convenience methods to manage a collection
  * Look at SessionsTests - already Kotlin
  * Talk about companion object, static etc
  * `nulls` test
    * show TypeCastException when we change the title
    * change cast to !! and show KotlinNullPointerException when we change the title
    * Show infers second reference cannot be null because of flow typing
    * Show the type of notNullSession given as or !!
  * Convert Sessions to Kotlin
  * Run tests
  * subtitleOf
    * Compare with Java - talk about ?. 
  * subtitleOrPrompt
    * Compare with Java - talk about ?:
  * Move Session static methods to top level scope - talk about static scope
  * Make into extension functions
  * Note use of extension functions on nullable types
  * Remove boilerplate
  * Convert subtitleOrPrompt to property (Alt-Enter)
  * Talk about properties v functions
  * Convert findWithTitle to Kotlin (remove .stream()) - note lambda syntax and destructuring
  * Remove the destructuring as unhelpful (even risky -- explain risks)
  * Talk about the difference between iterables and sequences
  * Use predicate form of firstOfNull
  * Run tests
  * Talk about API design by adding extension methods to existing types instead of defining new types 
  * typealias List<Session> to Sessions
  * run the tests, check diffs, talk about diffs, checkin

* Part 3: modules and functions
  * Look at JsonFormatTests 
    * note that we want to marshall session to and from JSON
  * Look at JsonFormat
    * we're groping towards a Java DSL for JSON, using Json
  * Look at Json
    * Try annotating `props` param of `obj` method with `@Nullable` so comments about nullability
      are not necessary -- you cannot!
    * Note use of Map.Entry - used as a pair. But Kotlin has a pair.
    * Import Map.Entry, replace Entry< with Pair<, fix issues, 
    * Run tests, checkin
    * Convert to Kotlin, applying changes to affected code
      * You'll get compiler errors - ignore them for now 
      * Look at the changes.  JsonFormat and JsonFormatTests are full of INSTANCE!
         * Explain Kotlin objects -- they are singletons!!! :scream-emoji: 
         * Revert.
      * We could annotate all methods in Json with @JvmStatic.  Or we could convert the dependent classes first.  Let's do the latter.
  * JsonFormatTests
    * Convert to Kotlin AND RERUN THE TESTS
    * They fail, because JUnit needs `approval` to be a field.  Annotate with @JvmField
    * Also mention the @Throws annotations, and then remove them
    * Run and checkin
  * JsonFormat
    * Convert to Kotlin - IJ doesn't do a very good job in the face of Java lambdas sometimes
    * Try the pedagogical object: java.util.function.Function<Presenter, JsonNode> fix
    * Fix compilation errors by removing explicit `Function<...>` SAM notation
    * Explain `it` variable in lambdas
    * Don't convert lambdas to references - do move them outside parameter list
    * That collect turns out to be `collect<List<Presenter>, Any>(Collectors.toList<Presenter>() as Collector<in Presenter, Any, List<Presenter>>?`
    * Run tests
    * Remove @Throws: it's not called from Java any more (we'll talk about type safe error handling later if we have time)
    * Convert streams code to Kotlin map/flatMap/etc. (Remember that JsonNode is iterable, so has map, etc. defined for it)
    * move functions to module scope
    * convert to extension methods on domain types and JsonNode
    * Test and checkin
  * Back to Json
    * Convert to Kotlin
    * To make it compile:
      * Use Kotlin's function type syntax instead of java.util.Function<T,U>
      * remove some explicit type params that are not needed
      * use nullable types to indicate that array and iterable *elements* can be null
    * move functions to module scope
    * remove streams
    * use infix to
    * observe `object`
    * replace props.forEach with filterNotNull().toMap()
    * in array use apply to initialise result ...
    * ... but then replace it with ArrayNode(nodes, elements.toList())
    * Convert functions to extension methods where applicable
    * We can get rid of Iterable<T>.array(fn) now
    * Convert `prop(name,value)` to `name of value` (infix function)
      * Discuss gradual introduction of mini-DSLs, rather than up-front DSL design which often ends up inflexible
  * Back to JsonFormat
    * convert the JSON as text into multiline strings
    * make extension properties from nonBlank functions
    * use isNullOrBlank
    * use let in Session.toJson
    
Themes

  * pragmatic language
  * Java interop
  * tooling
  * much less classy than Java
  * extension functions for fun and profit  
 
There is a lot we still haven't covered
  
  * delegation
  * sealed classes
  * when expressions
  * sequences 
  * inline functions
  * reified types in functions
  * coroutines
  * error handling
  * ...
