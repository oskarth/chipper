# Chipper

Chipper is a toy DSL for expressing and reasoning about basic logical
gates. Written during Hacker School in order to learn how to write a
DSL and to solve some problems in *The Elements of Computing Systems*.

- nand* is a primitive function
- Rest are defined by you as new building blocks
- We can bind one chip's output to another chip's input

In general, a logical gate consists of a spec and a implementation.
The spec specifies the name, input pins, output pins, and desired
behavior (optional comment). The implementation specifies how this is
accomplished, with the help of other, more primitive, boolean
functions.

## Usage

`(defgate GATE-NAME [INPUTS] => [OUTPUTS]
   (PART 1)
   ...
   (PART N))`

For example, we can define a and* gate like this

`(defgate and* [a b] => [out]
   (nand* [a b] => [w])
   (not* [w] => [out]))`

Of course, this assumes there is a not* chip to begin with. Note that
we don't care about *how* this is implemented, as long as it
implements the spec we have given it:

- Do what the name implies (not should reverse the input)
- Take w as a input
- Return a output map with the keyword name of the symbol out

All of this is expressed in this line: `(not* [w] => [out])`

Running `(and* 0 1)` evaluates to `[0]`

You can define your own logical functions in a similar manner.

## Ideas

- `(with-truth-table gate)` function (bind with meta)
- Boolean array support
- Return of n rather than [n] if there is just one return value
- Online REPL
- Visualization on gates
- Make an ALU
  
## License

Copyright © 2012 oskarth

Distributed under the Eclipse Public License, the same as Clojure.
