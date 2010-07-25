# Scala Implementation of Cuckoo Hashing

## Warning

The test code is currently broken, in process of fixing. The main code builds fine though.

## Building

Use [sbt](http://code.google.com/p/simple-build-tool/) with Scala 2.8. Once you have an `sbt`
script:

    $ sbt update compile

## Implementation outline

Follows the framework laid out by Erlingsson et. al:
choose a number of randomized hash functions, and a number of slots per bucket
in the table. Increasing either parameter will reduce retrieval performance but
allow higher table occupancy. A decent choice is 2 hash functions and 4 slots
per bucket, which allows around 93-95% occupancy before updates are likely to
fail, forcing a re-hash and table expansion.

### Hash functions

A standard multiplicative hash function. For a T-slot table, With 64-bit random
parameters A and B: `h(x) = (Ax + B) mod T`

## References
* <http://en.wikipedia.org/wiki/Cuckoo_hashing>
* [A cool and practical alternative to traditional hash
tables](http://www.ru.is/faculty/ulfar/CuckooHash.pdf), U. Erlingsson, M. Manasse, F. Mcsherry, 2006. 
* [Efficient Hash Probes on Modern Processors](http://domino.research.ibm.com/library/cyberdig.nsf/1e4115aea78b6e7c85256b360066f0d4/df54e3545c82e8a585257222006fd9a2!OpenDocument), Kenneth A. Ross, 2006.
