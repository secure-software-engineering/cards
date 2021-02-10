# System

A System is the (semantic) container element of the DSL. It is used to describe software systems and can contain multiple components. A model instance can only contain one system.

In addition to the components, the system also defines data types, componentKinds and links to a mapping model.
```
System dbExample {
	MappingModel: "foo/bar.mappingmodel"
    datatypes:
		UserQuery_DT, UserData_DT
	componentKinds {
		ComponentKind ExternalEntity,
		ComponentKind HumanUser extends ExternalEntity,
    } 
    components:
		AtomicComponent User {
			securityLevel 1
			componentKind HumanUser 
            ports {
				INOUTPort webPort ( UserQuery_DT, UserData_DT)
			}
			sourceOf {
				UserData_DT, UserQuery_DT
			}
		}
} 
```
## Component Kinds
Component kinds are hierarchical types used to define additional properties of components.

A component kind can only directly extend a single component kind.
```
componentKinds {
    ComponentKind ExternalEntity,
    ComponentKind HumanUser extends ExternalEntity
}
```
# Component

Components are used to define individual parts of a software system. 
Every component has a name. It is advisable to choose a unique name here.

Components can have additional fields:
- securityLevel: A value in [0..255]
- componentKind: A component kind to identify classes of components
- ports: Ports, which can be used to connect components within a CompositeComponent

## Port
Every port has a name.

Additionally, every Port has exactly one PortType, which can be one of
- INPort, ports only used to transmit data to the component
- OUTPort, ports only used to transmit data from the component
- INOUTPort, ports, that can act as both INPort and OUTPort

<----------- #TODO sourceOf for ports? ----------->

```
INOUTPort webPort ( UserQuery_DT , UserData_DT , ServerResponse_DT )
```

## AtomicComponent
In addition to the fields described above, AtomicComponents define one more field:
- sourceOf: A List of DataTypes, which are produced by this component
```
AtomicComponent User {
    securityLevel 1
    componentKind HumanUser 
    ports {
        INOUTPort webPort ( UserQuery_DT, UserData_DT)
    }
    sourceOf {
        UserData_DT, UserQuery_DT
    }
}
```
## CompositeComponent
In addition to the fields described for Components, CompositeComponents also define three more fields:
- componentParts: A List of ComponentParts, that are part of this CompositeComponent
- portconnectors: A List of PortConnectors, which connect ports of different components inside this CompositeComponent
- portMappings: A List of PortMappings, which connect a port of this CompositeComponent to a port of a component inside this CompositeComponent.

```
CompositeComponent serverEnvironment {
    ports {
        INOUTPort extPort ( )
    }
    componentParts {
        ComponentPart WebServerPart {
            component WebServer
            portParts {
                PortPart userPortportPart map userPort,
                PortPart dbPortportPart map dbPort
            }
        }
        ComponentPart dbConnectorPart {
            component dbConnector
            portParts {
                PortPart serverPortportPart map serverPort,
                PortPart dbPortportPart map dbPort
            }
        }
        ComponentPart SQLDatabasePart {
            component SQLDatabase
            portParts {
                PortPart dbPortportPart map dbPort
            }
        }
    }
    portconnectors {
        Connector con1 from WebServerPart.dbPortportPart to dbConnectorPart.serverPortportPart
        Connector con2 from dbConnectorPart.dbPortportPart to SQLDatabasePart.dbPortportPart
    }
    portmappings {
        PortMapping Mapping from extPort to WebServerPart.userPortportPart
    }
}
```

## ComponentPart

ComponentParts are the individual parts of a Composite Component.

Each component part has a name.
Additionally, every component part has a component, which can be either an AtomicComponent or a CompositeComponent.

If the component has ports, the ComponentPart also requires a 
- portParts: List of PortParts

```
ComponentPart WebServerPart {
    component WebServer
    portParts {}
}
```

### PortPart
Portparts map ports of a component to be accesible from the componentPart.
Every PortPart has a name and a port.
```
portParts {
    PortPart userPortportPart map userPort,
    PortPart dbPortportPart map dbPort
}
```

## PortConnector
To connect ports of different components inside the same composite component, one has to define a PortConnector inside the composite component.

A PortConnector consists of 
- a name
- a source component and PortPart
- a target component and PortPart

Order of source and target does not matter, as the data flow direction is only defined by the port types.
```
portconnectors {
    Connector con1 from WebServerPart.dbPortportPart to dbConnectorPart.serverPortportPart
    Connector con2 from dbConnectorPart.dbPortportPart to SQLDatabasePart.dbPortportPart
}
```

## PortMapping
To connect ports of a component to a component outside the parent composite component, one has to define a port mapping, which maps the port of the parent composite component to a port of a child component.

The port types have to be compatible, i.e. a OUTPort of a CompositeComponent can only be mapped to an OUTPort or INOUTPort of a child component.

```
portmappings {
    PortMapping Mapping from extPort to WebServerPart.userPortportPart
}
```
The port of the parent composite component can then be connected (or mapped) by defining a PortConnector or PortMapping respectively, inside the parent composite component of the parent composite component. 