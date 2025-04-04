# genartlib

[![Clojars Project](https://img.shields.io/clojars/v/genartlib.svg)](https://clojars.org/genartlib)
[![cljdoc badge](https://cljdoc.org/badge/genartlib/genartlib)](https://cljdoc.org/d/genartlib/genartlib/CURRENT)

<img src="dev-resources/ectogenesis-small.jpg" alt="Ectogenesis" title="Ectogenesis" align="right" width="250"/>

A Clojure library with simple utilities for creating generative artwork.

This library is built around [Quil](https://github.com/quil/quil), a Clojure wrapper around the [Processing](https://processing.org) framework. However, most of the functions are just mathematical utilies that could be used idependently.


To see and read about my artwork, visit [tylerxhobbs.com](https://tylerxhobbs.com) or follow me on [Instagram](https://instagram.com/tylerxhobbs) or [Twitter](https://twitter.com/tylerxhobbs).

## Usage

To install, add this to your dependencies in `project.clj`:

```clojure
[genartlib "1.2.0"]
```

## Dependencies

This version of genartlib is compatible with Processing 4.x and Quil 4.x. It has been tested with JDK 21.

The previous 0.x versions of genartlib were compatible with Processing/Quil 3.x and JDK 1.8.

## Contents

View the [API Docs](https://cljdoc.org/d/genartlib/genartlib/CURRENT).

The genartlib library has the following tools:

### Project Template

Under [project-template/](https://github.com/thobbs/genartlib/tree/master/project-template), you'll find the basic setup that I use for every new generative art project. This is geared towards creating static images.

### `genartlib.algebra`

The following algebra-ish functions are defined:
* `avg` - average
* `interpolate` / `interpolate-multi` - linear interpolation
* `rescale` - map from one range to another
* `line-intersection` - find the intersection of two lines
* `lines-intersection-point` - another way to find line intersections
* `slope` / `point-slope` - get the slope of a line
* `y-intercept` - get the y intercept point of a line
* `angle` / `point-angle` - get the angle between two points in radians
* `angular-coords` - calculate the offset location from a base point with angle and magnitude
* `point-dist` - distance between two points
* `point-to-line-dist` - distance from a point to a line

### `genartlib.geometry`

* `polygon-contains-point?` - a fast test for checking if a point falls inside a polygon
* `rotate-polygon` - rotates around the average center of the poly
* `shrink-polygon` - shrink by a ratio

### `genartlib.curves`

* `chaikin-curve` - a curve-smoothing algorithm
* `chaikin-curve-retain-ends` - a variation that preserves the original end points
* `curve-length` - calculate the total length of a curve
* `split-curve-with-step` - break up a curve into chunks with the given length
* `split-curve-into-parts` - break up a curve into chunks with equal length, given a number of parts to have
* `interpolate-curve` - find a point that is a given percentage along the length of a curve
* `line-simplification` - an implementation of the Ramer-Douglas-Peucker line simplification algorithm
* `trim-curve-to-bounds` - returns a seq of sub-curves that are entirely within the bounds of the image
* `trim-curve-start` - removes the specified length from the start of a curve
* `trim-curve-end` - removes the specified length from the end of a curve

### `genartlib.random`

* `gauss` - sample a gaussian probability distribution
* `abs-gauss` - basically shorthand for `(abs (gauss ....))`
* `gauss-range` - returns a seq of integers from zero to a value sampled from a Guassian distribution
* `triangular` - sample a triangular probability distribution
* `simple-triangular` - like `triangular`, but assumes a = 0 and b = c
* `pareto-sampler` / `pareto-sample` - sample a pareto probability distribution
* `random-point-in-circle` - uniform sampling of points within a circle
* `odds` - returns true or false with the given probability
* `choice` - pick from a list of items with uniform probability
* `weighted-choice` - pick from a list of items, each with an assigned probability
* `repeatable-shuffle` - a version of shuffle that uses Processing's Random, in order to ensure repeatability with the same seed
* `limited-shuffle` - performs `n` swaps on a seq in order to partially shuffle it

### `genartlib.plotter`

* `sort-curves-for-plotting` - sorts a seq of curves in order to minimize plotter travel distance

### `genartlib.poisson-disc`

* `poisson-disc-sample` - generate a seq of points from a Poisson disc set with a guaranteed minimum spacing

### `genartlib.capture`

This namespace contains a set of macros that are useful for capturing draw operations in a sketch, and
recording them (or translating) them to another format, such as an SVG that is suitable for plotting. The
nice part is that the sketch will still function as normal.

* `with-plotter-svg-capture` - create an svg file representing all line and shape operations that is suitable for plotting with an Axidraw. Note that Processing functions other than these two (e.g. ellipse, or bezier curves, or transformations) are not yet supported and are ignored.
* `with-command-capture` - captures many draw operations in an EDN format that is easy to manipulate with Clojure
* `command-replay`/`command-replay-file` - replays the results of `with-command-capture`

### `genartlib.utils`

* `w` and `h` - shorthand for expressing a length or position in terms of percentage of the image width or height - good for using a pseudo-vector approach to creating images
* `pi` - shorthand for `(* Math.PI value)`
* `set-color-mode` - set the color mode to HSV with ranges H [0, 360], S [0.0, 100.0], V [0.0, 100.0], alpha [0.0, 1.0]
* `in?` / `not-in?` - test if a seq contains an item
* `between?` - is a value inside an inclusive range?
* `enumerate` - turns a seq of items into a seq like ([0 item-0] [1 item-1] [2 item-2] ...)
* `zip` - combine two or more seqs into tuples
* `snap-to` - snap a value to a given window size, kind of like configurable rounding
* `vec-remove` - remove an item from a vector
* `distinct-key` - given a function f to uniquely identify items, extracts all of the distinct values in a collection


## License

Copyright © Tyler Hobbs

Distributed under the MIT License.
