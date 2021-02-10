# Documentation
This is the Wiki documentation of the threat modeling project.

We offer a domain specific language (DSL) for describing the system under investigation as well as all threat modeling relevant parts. We provide both a textual and a graphical editor for our DSL.

The system is described by a generic component-based system model consisting of components and their connections.
In addition, the security analyst can define security related *restrictions* and *assumptions*.
Restrictions express which components might be allowed to know which data.
Assumptions describe assumptions regarding the implementation of the components made during the design phase, e.g., that a specific component will never leak data containing a password in cleartext.
We also provide a static analysis that checks of the system meets all specified restriction with respect to the specified assumptions.

In the following, we describe all modeling and analysis features in more detail.
If you would like to try our tooling yourself, check our [Users Guide](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/UserGuide.md) for first steps! 

## About our component models
The following links can give additional semantic information for every part of our models.

[System](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/ComponentModels.md#System)

[Component](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/ComponentModels.md#Component)

[Connections](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/ComponentModels.md#Component#CompositeComponent#Connections)

## About our security definitions
We model our security definitions in assumptions and restrictions:

[Assumptions](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/SecurityDefinitions.md#Assumptions)

[Restictions](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/SecurityDefinitions.md#Restrictions)

[Sanitizer](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/SecurityDefinitions.md#Santizier)

[Groups](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/SecurityDefinitions.md#Groups)

## About our modeling tools
We offer a textual and graphical modeling tool, implemented using xtext and sirius respectively.

[Graphical Modeling](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/GraphicalEditor.md)

[Textual Modeling](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/TextualEditor.md)

[Exporting Data and Results](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/Exports.md)

[Importing other Models](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/Imports.md)

## About our Analysis
[Analyzer Documentation](https://github.com/jogeismann/securedataflowdsl/blob/develop/docs/Analyzer.md)