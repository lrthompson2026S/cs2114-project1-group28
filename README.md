# Console Yahtzee

Console Yahtzee is a Java 17 implementation of the classic Yahtzee dice game created for **CS 2114: Software Design &
Data Structures — Project 1: Learning to Scope**.

## Features

* 1–8 players
* Five dice with hold/release functionality
* Up to three rolls per turn
* Standard upper and lower Yahtzee scoring categories
* Upper-section bonus
* Score or scratch categories
* Input validation for invalid user input
* Full 13-round game with final scores

Bonus Yahtzees and Joker rules are not implemented.

## Compile and Run

The project requires **Java 17**.

The classpath must include:

```text
yahtzee/lib/student.jar
```

Compile the source files with `student.jar` included on the classpath, then run the program from `Main`.

## Testing

JUnit tests are included for the major classes and cover normal behavior, edge cases, and invalid input where
applicable.

Note that student.TestCase uses the deprecate SecurityManager. Use of the compile command may be required.
 ```-Djava.security.manager=allow```
The command may vary per IDE/System

## System Diagram

It should be noted that some changes from the original spec and system diagram have not yet been reflected.

[System Diagram](https://lucid.app/lucidchart/6054c0dc-3b4f-4cbe-8c5a-cde50b433e01/edit?invitationId=inv_9c13f777-e6d7-4844-89d1-8cf73fad4352&page=0_0#)

## Presentation

[Google Slides](https://docs.google.com/presentation/d/1d6sACB22j2vwYOw-HOYszOoYHvW4cNccwfus8-rB0eo/edit?usp=sharing)
