# CARDS
Component-based Assumptions and Restrictions for Dataflow Specifications

We offer a domain specific language (DSL) for describing the system under investigation as well as all threat modeling relevant parts. We provide both a textual and a graphical editor for our DSL.
The system is described by a generic component-based system model consisting of components and their connections.
In addition, the security analyst can define security related restrictions and assumptions.
Restrictions express which components might be allowed to know which data.
Assumptions describe assumptions regarding the implementation of the components made during the design phase, e.g., that a specific component will never leak data containing a password in cleartext.
We also provide a static analysis that checks of the system meets all specified restriction with respect to the specified assumptions.

# Using Our Tools

As our tool suite is implemented as an eclipse plugin, it obviously relies on the eclipse IDE. 
All plugins are developed and tested using eclipse 2020-03, support for other versions is not guranteed.

## Using pre-built binaries

You can use artifacts of our CI as a update site for eclipse plugins or have a look at our releases for more stable versions.

## Building The Artifacts Yourself

Make sure you have Maven installed, then inside the root directory of this repository, run `mvn clean verify` to build.
The update site will be available inside `de.uni_paderborn.swt.cards.update/target/repository/`.

# Documentation

Learn more about our concepts and tools in our [documentation section](https://github.com/secure-software-engineering/cards/blob/develop/docs/README.md)!