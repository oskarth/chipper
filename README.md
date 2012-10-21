# Chipper

DSL for expressing and reasoning about basic logical chips. Written
during Hacker School in order to learn how to write a DSL and to solve
some problems in *The Elements of Computing Systems*.

- nand* is a primitive function
- Rest are defined by you as new building blocks
- We can bind one chip's output to another chip's input

## Usage

`(defchip NAME [INPUTS] [OUTPUTS]
   (PARTS))`

For example, we can define a and* gate like this

`(defchip and* [a b] [out]
   (nand* [a b] [w])
   (not* [w] [out]))`

Of course, this assumes there is a not* chip to begin with. Note that
we don't care about *how* this is implemented, as long as it
implements the spec we have given it:

- Do what the name implies (not should reverse the input)
- Take w as a input
- Return a output map with the keyword name of the symbol out

All of this is expressed in this line: `(not* [w] [out])`

Running `(and* [0 1] [:foo])` evalutes to `{:foo 0}`

You can define your own logical functions in a similar manner.

## TODO

- Write tests
- BUG: Multiple outputs aren't returned
- Truth-table printer
- Implement more basic chips
- Support for n-bit m-way functions
- Make an ALU (arithmetic logic unit)

## License

Copyright © 2012 oskarth

Distributed under the Eclipse Public License, the same as Clojure.
