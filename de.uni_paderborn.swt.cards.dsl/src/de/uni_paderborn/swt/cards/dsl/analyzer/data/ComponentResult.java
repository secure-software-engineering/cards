package de.uni_paderborn.swt.cards.dsl.analyzer.data;

import java.util.List;
import java.util.Stack;

import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart;
import de.uni_paderborn.swt.cards.dsl.tmdsl.System;

public class ComponentResult {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + ((stack == null) ? 0 : stack.hashCode());
		result = prime * result + ((stackDerivedName == null) ? 0 : stackDerivedName.hashCode());
		//result = prime * result + ((traces == null) ? 0 : traces.hashCode());
		//result = prime * result + ((types == null) ? 0 : types.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentResult other = (ComponentResult) obj;
		if (stack == null) {
			if (other.stack != null)
				return false;
		} else if (!stack.equals(other.stack))
			return false;
		if (stackDerivedName == null) {
			if (other.stackDerivedName != null)
				return false;
		} else if (!stackDerivedName.equals(other.stackDerivedName))
			return false;
		if (traces == null) {
			if (other.traces != null)
				return false;
		} else if (!traces.equals(other.traces))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}

	Stack<Object> stack;
	String stackDerivedName = "";
	/**
	 * @return the stackDerivedName
	 */
	public String getStackDerivedName() {
		return stackDerivedName;
	}

	List<List<Step>> traces;
	
	/**
	 * @return the traces
	 */
	public List<List<Step>> getTraces() {
		return traces;
	}

	/**
	 * @return the types
	 */
	public List<List<DataPoint>> getTypes() {
		return types;
	}

	List<List<DataPoint>> types;
	
	public ComponentResult(Stack<Object> stack, List<List<Step>> traces, List<List<DataPoint>> types) {
		for (Object o : stack) {
			if (stackDerivedName.length() > 0)
				stackDerivedName += ".";
			
			if (o instanceof System)
				this.stackDerivedName += ((System) o).getName();
			if (o instanceof Component)
				this.stackDerivedName += ((Component) o).getName();
			if (o instanceof ComponentPart)
				this.stackDerivedName += ((ComponentPart) o).getName();
		}
		
		this.stack = stack;
		
		this.traces = traces;
		this.types = types;
}

	/**
	 * @return the stack
	 */
	public Stack<Object> getStack() {
		return stack;
	}
}
