Exercise for the tutorial "Learn You a Kotlin For All The Good It Will Do You"
==============================================================================

First create a new branch. Check in after each change.  This lets you
easily show how auto-converting code to Kotlin affects how its API
looks when used from Java

Suggested progress

* Part 1: class syntax and data classes
  * Presenter
    * note that this an immutable value class with a public final field 
    * convert to Kotlin
    * talk through the bits of the Kotlin class - ctor, property, methods
    * run tests
    * remove equals, hashcode, to string - show tests fail
    * make a data class - show tests now pass
    * remove the unneeded class body
    * go to checkin - note JsonFormat.java has changed - IntelliJ has changed the field access to a getter - talk about this
    * checkin
  * Session
    * note that this is an immutable value class with public fields, one of which is Nullable, and it defensively copies the presenters
    * also that we have 2 constructors - one a convenience vararg
    * convert to Kotlin
    * Note subtitle is a `String?` - talk about nullabilty
    * Note primary v secondary constructor, observe primary ctor invocation
    * Note we can have a free property - presenters, initialised in class body
    * Talk about init block, but then remove it
    * show tests pass, remove equals etc - show tests fail
    * convert to a data class
    * No need to wrap List in unmodifiableList: discuss List/MutableList split, show List defn
    * Observe spread operator in constructor, remove it and replace with presenters.toList() - discuss asList()
    * Remove empty ctor body
    * Convert withXxx methods to single expression  - note lack of new
    * Convert withXxx methods to invoke .copy (do via add argument names and talk about argument names)
    * ***** Duncan says don't Talk about extension functions a bit. 
      * Try to move withXxx methods out of class into extension methods ... but it makes Java code too ugly
    * run the tests, check diffs, talk about diffs, checkin
  * SessionTests  
    * convert to Kotlin
    * run tests
    * talk about "internal"
    * talk about "var" vs "val"
    * talk about lack of a "new" keyword -- classes look like, and can be used as, functions
    * talk about listOf vs Arrays.asList -- Kotlin stdlib has lots of useful collection methods
    * talk about lack of return type when Unit
    * Now all of our Session clients are Kotlin we can inline the copys, except for withPresenters, which we
      can make vararg
    * Now the copy invocations don't need testing
    * Move withPresenters methods out of class into extension ... much nicer in Kotlin, yeah?
    * Explain extension functions in more detail ... syntactic sugar for static methods
    * Move withPresenters into SessionTests where it is used to illustrate convenience extensions
    * Rename test to `illustrate convenience extension methods` and talk about names
    * run the tests, check diffs, talk about diffs, checkin
  * Slots  
    * Convert Slots.  It's all Kotlin!!! That was easy!!1!
    * run the tests, check diffs, talk about diffs, checkin


* Part 2: Null and nullability
  * Look at Sessions - a bunch of static convenience methods to manage a collection
  * Look at SessionsTests - already Kotlin
  * Talk about companion object, static etc
  * `nulls` test
    * show null-check with bang-bang instead of `as`
    * Kotlin infers second reference cannot be null because of flow typing
    * Change the title, and show bang bang you're dead
  * Convert Sessions to Kotlin
  * Talk about use of extension methods on existing types instead of defining new types 
  * `null_safe_access` test
    * lift the return out of the if
    * convert if to elvis operator
    * convert to extension method and give a better name
      * note - extension method on a _nullable_ type.  That means `this` can be null!
        For example...
  * `elvis` test
    * convert to extension method first
    * lift return
    * have to manually introduce elvis operator 


* Part 3: modules and functions
  * Json
    * Before converting to Kotlin...
      * Try annotating `props` param of `obj` method with `@Nullable` so comments about nullability
        are not necessary -- you cannot!
    * Convert to Kotlin, applying changes to affected code
      * look at the changes.  JsonFormat and JsonFormatTests are now FUGLY!  Revert.
      * We could annotate all methods in Json with @JvmStatic.  Or we could convert the dependent classes first.  Let's do the latter.
  * JsonFormatTests
    * Convert to Kotlin AND RERUN THE TESTS
    * They fail, because JUnit needs `approval` to be a field.  Annotate with @JvmField
  * JsonFormat
    * Convert to Kotlin & fix compilation errors by removing explicit `Function<...>` SAM notation
    * Explain `it` variable in lambdas
    * Convert lambdas to references
    * Remove @Throws: it's not called from Java any more (we'll talk about type safe error handling later if we have time)
    * Convert streams code to Kotlin map/flatMap/etc. (Remember that JsonNode is iterable, so has map, etc. defined for it)
    * move functions to module scope
    * convert to extension methods on domain types and JsonNode
  * Back to Json
    * Convert to Kotlin
    * To make it compile:
      * import kotlin.collections.Map.Entry, not java.util.Map.Entry
      * Use Kotlin's function type syntax instead of java.util.Function<T,U>
      * remove some explicit type params that are not needed
      * use nullable types to indicate that array and iterable *elements* can be null
    * Use Pair<String,JsonNode> instead of Map.Entry, and then use Kotlin's collection pipeline functions
    * move functions to module scope
    * Convert functions to extension methods where applicable
    * Convert `prop(name,value)` to `name of value` (infix function)
      * Discuss gradual introduction of mini-DSLs, rather than up-front DSL design which often ends up inflexible


* Part 4: Write new algorithmic code
  * Implement functions in Scheduling and run Suggestaconf


* Part 5: Address loss of type safety w.r.t. exceptions
  * Make SessionCode::parse return a SessionCode?
  * Introduce a Result<T> algebraic data type (sealed class hierarchy)
  * Make JsonNode.toXxx return Result<Xxx>
  * Introduce operations on Result<T>:
    * map :: (A)->B, Result<A> -> Result<B>
    * flatMap :: (A)->Result<B>, Result<A> -> Result<B>
    * all :: (List<Result<T>>) -> Result<List<T>>
    * apply :: ((A,B,C)->R, Result<A>, Result<B>, Result<C>)->Result<R>
