# Assumptions
Assumptions are used to declare facts about data flows or sanitization.

There are six different kinds of assumptions:
- ComponentAssumption:  
  Component never outputs a specific data group.  
  `Component User neverOut UserData` 

- ComponentSanitizerAssumption:  
  Component sanitizes data type using a specific sanitizer.  
  `Component User sanitizes UserData using UserDataSanitizer`

- ComponentFlowAssumption:  
  Component prevents data (either a specific DataGroup or in general) from flowing from one of its ports to another  
  `Component WebServer prevents DataFlow userPort -> dbPort of UserData`

- ComponentFlowSanitizationAssumption:  
  Component sanitizes data (either a specific DataGroup or in general) from one port to another  
  `Component WebServer sanitizes DataFlow userPort -> dbPort of UserData using SQLSanitizer`

- PortAssumption:
  Port never outputs data (either a specific DataGroup or in general)  
  `Port userPort neverOut UserData`

- PortSanitizerAssumption:
  Port sanitizes data of a specific DataGroup using a specific Sanitizer  
  `Port userPort sanitizes UserData using SQLSanitizer`

These assumptions are considered facts when validating the models and will be considered when deciding, whether restrictions are violated.
# Restictions
Restrictions are used to define rules for what data types are allowed to be handled by which components. 

Restrictions always apply for a whole group of data types and can either globally allow or prevent data types of this group. As the phrase suggests, a globally allowed data type is allowed to be handled by every component and component part in the system.

Opposing this, a globally prevented data type must not be handled by any component, and component part in the system.

```
insert allow and prevent group examples
```

## Refinements

Refinements define exceptions to global restrictions. For globally allow restrictions, they define what parts of the system must not handle a datatype. For globally prevent restrictions, they define what parts of the system are allowed to handle a datatype.

All refinements can optionally have a sanitization option, i.e. prevent refinements only apply for components/parts/groups if the data types in the group are not sanitized, allow refinements only apply if the data types are sanitized.

- ComponentGroupRefinement:  
  Prevent/Allow refinement is valid for one or more component groups, can optionally exclude one or more components of these groups.
- ComponentPartRefinement:  
  Prevent/Allow refinement is valid for one or more component parts.
- ComponentRefinement:  
  Prevent/Allow refinement is valid for one or more components.


# Sanitizer

Sanitizers are used to define sanitization behaviour, they only consist of a name and are used for restrictions and assumptions.

# Groups

There are three types of groups which can and must be defined to define restrictions and assumptions. Each group has a name.

- DataGroup:  
  One or more data types grouped together.
- ComponentGroup:  
  One or more components grouped together.
- PortGroup:
  One or more ports grouped togehter. Port groups are currently not used.