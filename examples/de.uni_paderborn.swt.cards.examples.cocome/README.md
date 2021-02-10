# CoCoME example
This example is based on [CoCoMe](https://cocome.org).
Our model is a slightly simplified version of the first proposed use case, the sale.

# Different Models
[CashDeskComponents.tmdsl](./CashDeskComponents.tmdsl) contains just the components and connections between them.  
[CashDeskDataTypes.tmdsl](./CashDeskDataTypes.tmdsl) includes all data types and states which components are considered a source for them.  
[CashDeskRestrictions.tmdsl](./CashDeskRestrictions.tmdsl) includes a dataflow restriction for credit card information.  
[CashDesk.tmdsl](./CashDesk.tmdsl) contains the completed model including components, data types, restrictions and assumptions.  