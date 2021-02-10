package de.uni_paderborn.swt.cards.dsl.analyzer.data;

import java.util.Stack;

import org.eclipse.emf.ecore.EObject;

import de.uni_paderborn.swt.cards.dsl.tmdsl.AllowGroupRestriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PreventGroupRestriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.System;

public class RestrictionViolation {


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dp == null) ? 0 : dp.hashCode());
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		result = prime * result + ((stack == null) ? 0 : stack.hashCode());
		result = prime * result + ((violatedRefinement == null) ? 0 : violatedRefinement.hashCode());
		result = prime * result + ((violatedRestriction == null) ? 0 : violatedRestriction.hashCode());
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
		RestrictionViolation other = (RestrictionViolation) obj;
		if (dp == null) {
			if (other.dp != null)
				return false;
		} else if (!dp.equals(other.dp))
			return false;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		if (stack == null) {
			if (other.stack != null)
				return false;
		} else if (!stack.equals(other.stack))
			return false;
		if (violatedRefinement == null) {
			if (other.violatedRefinement != null)
				return false;
		} else if (!violatedRefinement.equals(other.violatedRefinement))
			return false;
		if (violatedRestriction == null) {
			if (other.violatedRestriction != null)
				return false;
		} else if (!violatedRestriction.equals(other.violatedRestriction))
			return false;
		return true;
	}

	private EObject violatedRestriction;
	private EObject violatedRefinement;
	private DataPoint dp;
	String stackDerivedName = "";

	/**
	 * @return the self
	 */
	public Component getSelf() {
		return self;
	}

	private Component self;
	private Stack<Object> stack;
	
	/**
	 * @return the violatedRestriction
	 */
	public EObject getViolatedRestriction() {
		return violatedRestriction;
	}

	/**
	 * @return the violatedRefinement
	 */
	public EObject getViolatedRefinement() {
		return violatedRefinement;
	}
	
	public String getStackDerivedName() {
		return stackDerivedName;
	}

	/**
	 * @return the dp
	 */
	public DataPoint getDp() {
		return dp;
	}

	public RestrictionViolation(EObject violatedRestriction, EObject violatedRefinement, DataPoint dp, Stack<Object> stack) {
		this.violatedRestriction = violatedRestriction;
		this.violatedRefinement = violatedRefinement;
		this.dp = dp;
		this.self = (Component) stack.peek();
		this.stack = stack;
		
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
		
	}

	@Override
	public String toString() {
		String s = "";
		s +=  "RestrictionViolation = [Data=" + dp.getType().getName() + ", ";
		s +=  "violatedRestriction=" + restrictionPrinter(violatedRestriction);
		
		
		if (violatedRefinement != null) {
			s += ", violatedRefinement="+ refinementPrinter(violatedRefinement);
		} 
		
		s+= " at component ";
		s+= this.stackDerivedName;
		s+= "]";
		return s;
	}
	
	private String restrictionPrinter(EObject res) {
		String s = "[";
		if (res instanceof AllowGroupRestriction) {
			s += "AllowGroupRestriction for ";
			AllowGroupRestriction agr = (AllowGroupRestriction) res;
			s += agr.getGroupAsset().stream().map(dataGroup -> dataGroup.getName()).reduce((a,b) -> a + ", " + b).get();
			s+= "";
		} else if (res instanceof PreventGroupRestriction) {
			s += "PreventGroupRestriction for ";
			PreventGroupRestriction pgr = (PreventGroupRestriction) res;
			s += pgr.getGroupAsset().stream().map(dataGroup -> dataGroup.getName()).reduce((a,b) -> a +"" + b).get();
			s+= "";	
		}
		
		s += "]";
		return s;
	}
	
	private String refinementPrinter(EObject ref) {
		String s = "[";
		
		// TODO add toString
		s+= ref.getClass().getSimpleName();
		
		s += "]";
		return s;
	}

	public Stack<Object> getStack() {
		return stack;
	}
	
}
