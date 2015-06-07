# Artificial Neural Networks

This JavaFX application shows, for each of its tabs, a little example about a type of artificial neural network.

## Pattern Associator (PA)

![PA image](http://i60.tinypic.com/vhdk54.jpg)

A pattern associator learns associations between input patterns and output patterns. One of the most appealing characteristics of such a network is the fact that it can generate what it learns about one pattern to other similar input patterns. Pattern associators have been widely used in distributed memory modeling.

The pattern associator is one of the more basic two-layer networks. Its architecture consists of two sets of units, the input units and the output units. Each input unit connects to each output unit via weighted connections. Connections are only allowed from input units to output units. The effect of a unit ui in the input layer on a unit uj in the output layer is determined by the product of the activation ai of ui and the weight of the connection from ui to uj. The activation of a unit uj in the output layer is given by: SUM(wij * ai).


## Hopfield Network

![Hopfield image](http://i57.tinypic.com/2wog4dl.jpg)

A Hopfield network is a form of recurrent artificial neural network invented by John Hopfield in 1982. Hopfield nets serve as content-addressable memory systems with binary threshold nodes. They are guaranteed to converge to a local minimum, but convergence to a false pattern (wrong local minimum) rather than the stored pattern (expected local minimum) can occur.
