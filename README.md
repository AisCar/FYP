# FYP
Repository for Final Year Project


## The Short Version
Using a genetic algorithm to generate optimal (halting) Turing machines w.r.t. their Busy Beaver score (number of ones they write to a tape).

## The Slightly Longer Version
The aim of the busy beaver game is to find the halting n-state Turing machine, for any natural number n, which writes the highest number of ones to an infinite length of tape.
The busy beaver score,  Σ(n), is the number of ones that that n-state Turing machine writes to a tape before it halts.

Although this is a simple concept, the number of n-state Turing machines
grows rapidly as n increases, so it is difficult to compare all of them.

This project uses a genetic algorithm to generate optimal Turing machines, using Σ(n) in its fitness function. 



## Code in this Repository

The code includes:
- a Turing machine emulator
- a genetic algorithm implementation
- code that generates an intial population of Turing machines for the genetic algorithm
- a simple GUI
- tests
