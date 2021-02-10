## Introduction

Our component model analysis works in three distinct steps, which are further explained in their respective chapters below. 
At first, we perform a depth-first approach to building possible paths through the model (graph) for each component and component part. After we built all the paths, we call them traces, we check every trace for all [assumptions](https://github.com/secure-software-engineering/cards/blob/develop/docs/SecurityDefinitions.md#Assumptions) made to the model. This gives us a list of data types (maybe sanitized), which the root component can receive on this trace.

Afterwards, we check if this list of data types violates any [restrictions](https://github.com/secure-software-engineering/cards/blob/develop/docs/SecurityDefinitions.md#Restrictions)

## How the analyzer builds traces
The analyzer builds traces in a depth-first manner. Every component(part) of the model will get a list of traces (paths) through the model.
In other words, we pick a component(part) from the model, and iterate over all ports of that component, checking connections to other components and thus building a list of all possible paths to other components. Here, no loops are possible.
At every step, we remember the source and target port of the connection (or mapping).
Because the analyzer works recursively, a few extra steps are neccessary to make this work; Without giving to much technical detail, we build a stack of parent components(parts) for every method call, so we can easily leave a composite component, if for example we started the algorithm for a component part and found a port mapping connecting a port to a component, which lies outside our parent composite component.

## How we analyze assumptions and build a list of possible data types for each component
Given a trace and all assumptions made to the model, our assumption analysis works as follows:
We start at the component which is furthest away from the root (directed connections, the furthest away component might produce data which will be sanitized on the way). 
For this component, we add all data types, for which this component is a source, to a list. Then, step by step, we check whether any assumption prohibits transmission of this data type or adds sanitization to this data type, adding new data types from other components on the way. In the end, this leaves us with a set of data types, which might be sanitized, for each trace and thus gives us information about which data a component can receive (union of all sets), or information about which data it can receive on a given trace.

## How we check against restrictions to find violations
Now that we know, what data each component can receive, we can easily check whether our model conforms to our restrictions by checking every data type (+ sanitization option) against each restriction.
Our analysis then allows us to print out every violation by listing the violated restriction, refinement, the data type which violates it and the component. Because we retain all information, we could even print the exact trace and thus (theoretically) give a suggestion on where to add an assumption to the model, to fix the violation. This however, is not yet implemented.