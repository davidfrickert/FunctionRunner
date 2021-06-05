# FaaS-GraalVM - MSc Thesis

Main repository of a Serverless Framework that supports running functions that are compiled with the framework in a GraalVM Native Image instance.
Using Isolates, it's possible to execute concurrently the same function ensuring data separation, since the function code is executed inside Isolates, which provide a disjunct heap, similar to V8 Engine Isolates.
