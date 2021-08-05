

# AUTHORIZER

I have selected [Scala](https://www.scala-lang.org/) using [SBT](https://www.scala-sbt.org/index.html), implementing  [Onion Architecture](https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/) to solve Code Challenge: Authorizer.

I choose Onion architecture because It has following qualities:
- Testable
- Decoupled layers
- Easy to maintain
- Very extensible

And, Scala offer me following advantages:
- Functional programming style
- The Scala collections classes have a very functional API
- Scala runs on the JVM
- Strongly typed
- I feel more comfortable

And, to handle the stdin and stdout I choose [Akka stream](https://doc.akka.io/docs/akka/2.5.32/stream/index.html) because It offer me following advantages:
- Fulfill specification http://www.reactive-streams.org/
- Publisher = emits elements (Async)
- Suscriber = receive elements
- Processor = transform elements
- Handle backpressure
- Handle fault tolerance

## How to run

    docker build -t authorizer . 
    docker run authorizer:latest 

> Replace or edit operations file to new operations

### Note
Thanks to the oportunity and I'm open to feedback