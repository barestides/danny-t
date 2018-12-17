# danny-t
Parallel maze solving algorithm.

Danny Torrance was good at solving mazes, or at least one maze. He also implements
a fake parallel maze solving algorithm in order to trick his axe-crazy father and
escape.

## Setup
This project requires the [meiro maze generation toolkit](https://github.com/defndaines/meiro).

This will need to be installed locally to your .m2 folder. Go [here](https://stackoverflow.com/questions/36675246/how-to-use-a-local-repository-for-a-clojure-library-during-initial-development) if you don't know how to do this. I did not know how to do this.

## Usage
Load the main (only) namespace, `danny-t.core` into a repl.
Use `solve-maze` to compare time results between the parallel solver this project supplies
and the sequential solver supplied by the meiro library.
