package de.uni_paderborn.swt.cards.dsl.analyzer.data;

import java.util.Stack;

import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port;

public class Step {
	public Stack<Object> stack;
	public ComponentPart cp;
	public Component c;
	public Port p;
	public boolean isSource;
	
	public Step(Stack<Object> stack, ComponentPart cp, Component c, Port p, Boolean isSource) {
		this.stack = stack;
		this.cp = cp;
		this.c = c;
		this.p = p;
		this.isSource = isSource;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		
		Step s = (Step) obj;
		if (this.cp != s.cp)
			return false;
		if (this.c != s.c) 
			return false;
		if (this.p != s.p)
			return false;
//		if (this.isSource != s.isSource)
//			return false;
		if (s.stack != null) {
			
			for (Object o : s.stack) {
				if (s.stack.indexOf(o) != this.stack.indexOf(o)) {
					return false;
				}
			}
			for (Object o : this.stack) {
				if (s.stack.indexOf(o) != this.stack.indexOf(o)) {
					return false;
				}
			}

		}
//		System.out.println(this.stack);
//		System.out.println(s.stack);
//		System.out.println(this.cp);
//		System.out.println(s.cp);
//		System.out.println(this.c);
//		System.out.println(s.c);
//		System.out.println(this.p);
//		System.out.println(s.p);
//		System.out.println("-");
		return true;
	}
	
	@Override
	public String toString() {
		if (p == null) 
			return "Step: " + (cp != null ? cp.getName() : "") + c.getName() + " atomic";
		
		//return "Step: "  + c.getName() + "." + p.getName();
		return "Step: " + (cp != null ? cp.getName() + "." : "") + c.getName() + "." + p.getName();
	}
}
