package de.uni_paderborn.swt.cards.dsl.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.Step;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.CompositeComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortConnector;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortMapping;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType;
import de.uni_paderborn.swt.cards.dsl.export.TraceExporter;

public class Analyzer {

	public static void propagateAndPrint(Model m) {

		List<ComponentResult> results = analyzeModel(m);
		System.out.println("finished calc");
		System.out.println(new TraceExporter(results).generate());
		System.out.println("---------");
		System.out.println(RestrictionAnalyzer.checkRestrictions(m.getRestriction(), results));

	}
	
	public static List<ComponentResult> analyzeModel(Model m) {
		Assumption assumption = m.getAssumption();
		List<ComponentAssumption> componentAssumptions = assumption.getComponentAssumptions();
		List<ComponentSanitizerAssumption> componentSanitizerAssumptions = assumption.getComponentSanitzerAssumptions();
		List<ComponentFlowAssumption> componentFlowAssumptions = assumption.getFlowAssumptions();
		List<ComponentFlowSanitizationAssumption> componentFlowSanitizationAssumption = assumption.getSanitizersAssumptions();
		List<PortAssumption> portAssumptions = assumption.getPortAssumptions();
		List<PortSanitizerAssumption> portSanitizerAssumptions = assumption.getPortSanitizerAssumptions();
		
		List<EObject> assumptions = new ArrayList<EObject>();
		assumptions.addAll(componentAssumptions);
		assumptions.addAll(componentSanitizerAssumptions);
		assumptions.addAll(componentFlowAssumptions);
		assumptions.addAll(componentFlowSanitizationAssumption);
		assumptions.addAll(portAssumptions);
		assumptions.addAll(portSanitizerAssumptions);
		
		return analyzeModel(m, assumptions);
	}
	

	public static List<ComponentResult> analyzeModel(Model m, List<EObject> assumptions) {
		//System.out.println("started analysis");
		de.uni_paderborn.swt.cards.dsl.tmdsl.System sys = m.getSystem();

		List<ComponentResult> results = new ArrayList<ComponentResult>();
		
		List<Component> comps = sys.getComponents();
		
		for(Component c : comps) {
			if (c instanceof CompositeComponent)
				if (checkCompositeForLoops((CompositeComponent) c))
					return results;
		}
		
		
		if (comps.isEmpty())
			return results;

		for (Component c : comps) {
			//if(!c.getName().equals("In_Out_Mapping_Loop_CC")) continue;
			// if (!c.getName().equals("serverEnvironment")) continue;
			Stack<Object> stack = new Stack<Object>();
			stack.add(sys);

			if (c instanceof CompositeComponent) {
				results.addAll(anaylzeCompositeComponent(c, assumptions, copyStack(stack)));
			} else {

				// if (true) continue;
				List<List<Step>> l = propagate(stack, c, new ArrayList<Step>());

				List<List<DataPoint>> types = AssumptionsAnalyzer.analyzeDataTypes(assumptions, l);

				
				for (int i = 0; i < types.size(); i++) {
					types.set(i, new ArrayList<DataPoint>(new HashSet<DataPoint>(types.get(i))));
				}

				stack.add(c);
				results.add(new ComponentResult(copyStack(stack), l, types));
				stack.pop();

//				System.out.println("All possible paths for " + c.getName());
//				System.out.println(l + "\n\n");
			}

		}
		
		results = new ArrayList<ComponentResult>(new HashSet<ComponentResult>(results));
		
		// remove results for composite components 
		results = results.stream().filter(cr ->!(cr.getStack().peek() instanceof CompositeComponent)).collect(Collectors.toList());
		
		return results;
	}

	public static boolean checkCompositeForLoops(CompositeComponent c) {
		Stack<CompositeComponent> stack = new Stack<CompositeComponent>();
		return loopCheckRecursion(stack, c);
	}
	
	private static boolean loopCheckRecursion(Stack<CompositeComponent> stack, CompositeComponent c) {
		
		if (stack.contains(c)) {
//			System.out.println("found match");
//			System.out.println(stack);
//			System.out.println(c);
			return true;
		}
		
		boolean loop = false;
		
		stack.push(c);
		for (ComponentPart cp : c.getComponentParts()) {
			if (cp.getComponent() instanceof CompositeComponent) {
				
				loop = loopCheckRecursion(stack, (CompositeComponent) cp.getComponent());
			}
		}
		stack.pop();
		
		return loop;
	}

	private static List<ComponentResult> anaylzeCompositeComponent(Component c, List<EObject> assumptions,
			Stack<Object> stack) {
		List<ComponentResult> results = new ArrayList<ComponentResult>();
		// if (!c.getName().equals("a")) continue;
		//if(! (c.getName().equals("In_Out_Mapping_Loop_CC") ||c.getName().equals("Triple_IO_CC" ))) return results;

		List<List<Step>> traces = propagate(stack, c, new ArrayList<Step>());
//		System.out.println("All possible paths for " + c.getName());
//		System.out.println(traces + "\n\n");
		List<List<DataPoint>> types = AssumptionsAnalyzer.analyzeDataTypes(assumptions, traces);
		
		for (int i = 0; i < types.size(); i++) {
			types.set(i, new ArrayList<DataPoint>(new HashSet<DataPoint>(types.get(i))));
		}
		
		stack.add(c);
		results.add(new ComponentResult(copyStack(stack), traces, types));

		for (ComponentPart cp : ((CompositeComponent) c).getComponentParts()) {
			// if (!cp.getName().equals("SQLDatabasePart")) continue;
			// System.out.println("__");
			// if (!cp.getName().equals("a")) continue;
			stack.push(cp);
			List<List<Step>> l = propagate(stack, cp.getComponent(), new ArrayList<Step>());
			stack.pop();

			List<List<DataPoint>> types1 = AssumptionsAnalyzer.analyzeDataTypes(assumptions, l);

			for (int i = 0; i < types1.size(); i++) {
				types1.set(i, new ArrayList<DataPoint>(new HashSet<DataPoint>(types1.get(i))));
			}

//				System.out.println("All possible paths for " + c.getName() + "." + cp.getName());
//				System.out.println(l + "");
//				System.out.println("types:\n" + types1 + "\n\n\n");

			stack.push(cp);
			stack.push(cp.getComponent());
			results.add(new ComponentResult(copyStack(stack), l, types1));
			stack.pop();
			stack.pop();

			if (cp.getComponent() instanceof CompositeComponent) {
				CompositeComponent cpcc = (CompositeComponent) cp.getComponent();
				stack.push(cp);
				results.addAll(anaylzeCompositeComponent(cpcc, assumptions, copyStack(stack)));
				stack.pop();
			}

		}

		return results;
	}

	private static List<List<Step>> propagate(Stack<Object> parentStack, Component c, List<Step> trace) {

		List<List<Step>> allTraces = new ArrayList<List<Step>>();
		boolean hasParent = false;
		boolean endOfPath = true;
		boolean hasPart = false;
		CompositeComponent parentCompositeComponent = null;
		ComponentPart parentCompositeComponentComponentPart = null; // the "parent" CP of the CC
		ComponentPart compPart = null;

		if (parentStack.peek() instanceof ComponentPart) {
			hasPart = true;
			compPart = (ComponentPart) parentStack.peek();
			// System.out.println(compPart.getName());
		}

		if (hasPart) {
			parentStack.pop();
		}

		if (parentStack.peek() instanceof CompositeComponent) {
			hasParent = true;
			parentCompositeComponent = (CompositeComponent) parentStack.pop();

			if (parentStack.peek() instanceof ComponentPart) {
				parentCompositeComponentComponentPart = (ComponentPart) parentStack.peek();
			}

			parentStack.push(parentCompositeComponent);
		}

		if (hasPart) {
			parentStack.push(compPart);
		}
		
		if (c instanceof AtomicComponent) {
			AtomicComponent atomicComponent = (AtomicComponent) c;

			if (!hasParent) {
				// this component is boring on its own, the only dataTypes are their own.
				trace.add(new Step(copyStack(parentStack), compPart, c, null, true));
				allTraces.add(trace);
				return allTraces;
			}

			for (Port port : atomicComponent.getPorts()) {
				if (port.getPortType() == PortType.OUT) {
					// we dont care, because no dataTypes can enter this component through this
					// port.
					continue;
				}

				for (PortConnector portConnection : parentCompositeComponent.getPortconnectors()) {
					ComponentPart sourcePart = portConnection.getSourcePart();
					Component sourceComponent = sourcePart.getComponent();
					Port sourcePort = portConnection.getSource().getPort();

					ComponentPart targetPart = portConnection.getTargetPart();
					Component targetComponent = targetPart.getComponent();
					Port targetPort = portConnection.getTarget().getPort();

					if (sourcePort == port && sourceComponent == atomicComponent && sourcePart == compPart) {
						if (targetPort.getPortType() == PortType.IN) {
							// own port is INOUT, remote port is in -> we can not receive any data on this
							// connection
							continue;
						}
						Step s1 = new Step(copyStack(parentStack), sourcePart, sourceComponent, sourcePort, false);
						
						Object temp = parentStack.pop();
						parentStack.push(targetPart);
						Step s2 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, true);
						parentStack.pop();
						parentStack.push(temp);
						
						if (isPathDuplicate(trace, s1, s2))
							continue;
						endOfPath = false;

						List<Step> newTrace = copyListAndAdd(trace, s1, s2);

						// keep the stack the same, as we are exploring components on the same
						// hierarchy, but add ComponentPart
						temp = parentStack.pop();
						parentStack.push(targetPart);
						allTraces.addAll(propagate(parentStack, targetComponent, newTrace));
						parentStack.pop();
						parentStack.push(temp);
					} else if (targetPort == port && targetComponent == atomicComponent && targetPart == compPart) {
						if (sourcePort.getPortType() == PortType.IN) {
							// own port is INOUT, remote port is in -> we can not receive any data on this
							// connection
							continue;
						}
						Step s1 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
						Object temp = parentStack.pop();
						parentStack.push(sourcePart);
						Step s2 = new Step(copyStack(parentStack), sourcePart, sourceComponent, sourcePort, true);
						parentStack.pop();
						parentStack.push(temp);

						if (isPathDuplicate(trace, s1, s2))
							continue;
						endOfPath = false;

						List<Step> newTrace = copyListAndAdd(trace, s1, s2);

						// keep the stack the same, as we are exploring components on the same
						// hierarchy, but add CompPart
						temp = parentStack.pop();
						parentStack.push(sourcePart);
						allTraces.addAll(propagate(parentStack, sourceComponent, newTrace));
						parentStack.pop();
						parentStack.push(temp);
					}
				}

				for (PortMapping portMapping : parentCompositeComponent.getPortMappings()) {
					Port sourcePort = portMapping.getSource();
					ComponentPart targetPart = portMapping.getTargetPart();
					Component targetComponent = targetPart.getComponent();
					Port targetPort = portMapping.getTarget().getPort();

					if (sourcePort == port) {
						// this should never happen: port is the port of an atomic component, but
						// sourcePort is always a port of a composite component
						System.out.println("Abandon your posts! Flee, flee for your lives!");
						System.out.println(
								"No, seriously. Something is very very wrong with your model. Or this algorithm. See me after class!");
					} else if (targetComponent == atomicComponent && targetPort == port && targetPart == compPart) {
						// this port of the component currently under review is mapped to the port of
						// its parentCompositeComponent
						// this means we can receive dataTypes from outside our parents scope

						// CompositeComponent parent = (CompositeComponent) parentStack.peek();
						CompositeComponent parent = parentCompositeComponent;

						Step s1 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
						ComponentPart temp2 = (ComponentPart) parentStack.pop();
						CompositeComponent temp = (CompositeComponent) parentStack.pop();
						Step s2 = new Step(copyStack(parentStack), parentCompositeComponentComponentPart, parent, sourcePort, true);
						parentStack.push(temp);
						parentStack.push(temp2);

						if (isPathDuplicate(trace, s1, s2))
							continue;
						endOfPath = false;
						
//						if(targetComponent.getName().equals("WebServer_AC")) {
//							System.out.println(parentStack);
//							System.out.println(trace);
//							System.out.println("" + s1.cp + s1.c + s1.p);
//							System.out.println("" + s2.cp + s2.c + s2.p);
//						}

						List<Step> newTrace = copyListAndAdd(trace, s1, s2);

						// we are moving one scope higher, so pop the stack but remember the element for
						// next loop iteration
						temp2 = (ComponentPart) parentStack.pop();
						temp = (CompositeComponent) parentStack.pop();
						// TODO is the component port still there?
						allTraces.addAll(propagate(parentStack, parent, newTrace));
						parentStack.push(temp);
						parentStack.push(temp2);
						
					}

				}
			}

		} else if (c instanceof CompositeComponent) {
			CompositeComponent compositeComponent = (CompositeComponent) c;
			if (!hasParent) {
				// check if trace is empty, because then we are really only looking at the component itself, so we can look at all its port

				
				if (!trace.isEmpty() && trace.get(trace.size() - 1).c == compositeComponent) {
					// we left through an in-mapping of this composite component and are not interested in what can be output by the comp itself
					// the component has no parent, so there is nothing more to explore
					allTraces.add(trace);
					return allTraces;
				}
				// in this case:
				// I am always directly in the system, no bigger picture -> no connections on
				// the outside of this component
				// still, we want to know what datatypes this component "can output/have"
				// in other cases it was just "I can output everything I a) produce or b) get on
				// my imputs

				// but here, it is just what is connected to my output
				// hence we just take a look at the output ports of this component and continue
				// as before
				// in particular, we are only interested in the mappings

				for (Port port : compositeComponent.getPorts()) {
					if (port.getPortType() == PortType.IN) {
						// we have no bigger picture, hence the component does not learn anything new
						// here.
						// this trace ends here, but might still be interesting!
						// if it is interesting, it will be included in a trace, as it needs to reach
						// either an atomic component
						// or a INOUT port of the compositeComponent.. (dangling ports are boring)
						continue;
					} else {
						for (PortMapping portMapping : compositeComponent.getPortMappings()) {
							Port sourcePort = portMapping.getSource();
							ComponentPart targetPart = portMapping.getTargetPart();
							Component targetComponent = targetPart.getComponent();
							Port targetPort = portMapping.getTarget().getPort();

							if (sourcePort == port) {
								// flipped isSource here, because the source is our own port TODO: rename
								// portMapping source and target to match mapping scheme, not connection scheme
								Step s1 = new Step(copyStack(parentStack), compPart, compositeComponent, sourcePort, true);
								parentStack.push(compositeComponent);
								parentStack.push(targetPart);
								Step s2 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
								parentStack.pop();
								parentStack.pop();

//								if(targetComponent.getName().equals("WebServer_AC")) {
//									System.out.println(parentStack);
//									System.out.println(trace);
//									System.out.println("" + s1.cp + s1.c + s1.p);
//									System.out.println("" + s2.cp + s2.c + s2.p);
//									System.out.println(isPathDuplicate(trace, s1, s2));
//								}
								
								if (isPathDuplicate(trace, s1, s2)) {
									continue;
								}
								
//								System.out.println("not dup");
								// take this connection: copy the trace as we pass it by reference
								List<Step> newTrace = copyListAndAdd(trace, s1, s2);

								endOfPath = false;

								// I am always going inside this composite component, hence add "self" to stack
								parentStack.push(compositeComponent);
								parentStack.push(targetPart);
								allTraces.addAll(propagate(parentStack, targetComponent, newTrace));
								parentStack.pop();
								parentStack.pop();

							} else if (targetComponent == compositeComponent && targetPort == port) {
								// this should never happen!
								System.out.println("Abandon your posts! Flee, flee for your lives!");
								System.out.println(
										"No, seriously. Something is very very wrong with your model. Or this algorithm. See me after class!");

							}
						}
					}
				}

				// I am done here, because I explored every meaningful port of this
				// compositeComponent
				//System.out.println(allTraces.get(allTraces.size()-1));
				//return allTraces;
			}

			if (trace.size() > 0) {

				Step lastStep = trace.get(trace.size() - 1);

				// found the connection pair we entered this composite component with
				// we are now interested in where it is connected to.
				// remember: we entered using a port mapping, but we might be either inner or
				// outer shell (from where we were called)
				// hence we need to take a look at both mappings and connectors of the parent
				if (hasParent) {
					// CompositeComponent parentComponent = (CompositeComponent) parentStack.peek();
					for (PortConnector portConnector : parentCompositeComponent.getPortconnectors()) {
						ComponentPart sourcePart = portConnector.getSourcePart();
						Component sourceComponent = sourcePart.getComponent();
						Port sourcePort = portConnector.getSource().getPort();

						ComponentPart targetPart = portConnector.getTargetPart();
						Component targetComponent = targetPart.getComponent();
						Port targetPort = portConnector.getTarget().getPort();
						
						if (sourcePart == lastStep.cp && sourceComponent == lastStep.c && sourcePort == lastStep.p) {
	//						// context: we just left a compositeComponent through a compositeComponent port
	//						// and found a portConnector which connects our compositeComponent to some component
	//						// hence we are only interested in data types coming FROM the remote component
								
							if (targetPort.getPortType() == PortType.IN) {
								continue;
							}
							
							Step s1 = new Step(copyStack(parentStack), sourcePart, sourceComponent, sourcePort, false);
							Object temp = parentStack.pop();
							parentStack.push(targetPart);
							Step s2 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, true);
							parentStack.pop();
							parentStack.push(temp);
							
							if (isPathDuplicate(trace, s1, s2))
								continue;
							endOfPath = false;
							
							List<Step> newTrace = copyListAndAdd(trace, s1, s2);
							
							// keep the stack the same, as we are exploring components on the same hierarchy
							temp = parentStack.pop();
							parentStack.push(targetPart);
							allTraces.addAll(propagate(parentStack, targetComponent, newTrace));
							parentStack.pop();
							parentStack.push(temp);
							
						} else if (targetPart == lastStep.cp && targetComponent == lastStep.c && targetPort == lastStep.p) {
							// context: we just left a compositeComponent through a compositeComponent port
							// and found a portConnector which connects our compositeComponent to some
							// component
							// hence we are only interested in data types coming FROM the remote component
							if (sourcePort.getPortType() == PortType.IN) {
								continue;
							}
							Step s1 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
							Object temp = parentStack.pop();
							parentStack.push(sourcePart);
							Step s2 = new Step(copyStack(parentStack), sourcePart, sourceComponent, sourcePort, true);
							parentStack.pop();
							parentStack.push(temp);

//							if (targetComponent.getName().equals("ServerEnvironment_CC")) {
//								System.out.println(trace);
//								System.out.println(s1);
//								System.out.println(s2);
//							}
							
							if (isPathDuplicate(trace, s1, s2))
								continue;
							
//							System.out.println("not duplicate");
							endOfPath = false;

							List<Step> newTrace = copyListAndAdd(trace, s1, s2);

							// keep the stack the same, as we are exploring components on the same hierarchy
							temp = parentStack.pop();
							parentStack.push(sourcePart);
							allTraces.addAll(propagate(parentStack, sourceComponent, newTrace));
							parentStack.pop();
							parentStack.push(temp);
						}

					}
				}
				for (PortMapping portMapping : compositeComponent.getPortMappings()) {
					Port sourcePort = portMapping.getSource();
					ComponentPart targetPart = portMapping.getTargetPart();
					Component targetComponent = targetPart.getComponent();
					Port targetPort = portMapping.getTarget().getPort();

					if (sourcePort == lastStep.p) {
						// beispiel: wir wurden bspw von A aus aufgerufen, A ist mit ca_in zu inner_exit
						// gegangen, wir sind jetzt
						// der aufruf mit component inner und gucken uns gerade port exit an,
						// jetzt wollen wir cd_out finden

						Step s1 = new Step(copyStack(parentStack), compPart, compositeComponent, sourcePort, false);
						parentStack.push(compositeComponent);
						parentStack.push(targetPart);
						Step s2 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, true);
						parentStack.pop();
						parentStack.pop();

						if (isPathDuplicate(trace, s1, s2))
							continue;
						endOfPath = false;

						List<Step> newTrace = copyListAndAdd(trace, s1, s2);

						// we are diving into this compositeComponent, hence add it to the stack
						parentStack.push(compositeComponent);
						parentStack.push(targetPart);
						allTraces.addAll(propagate(parentStack, targetComponent, newTrace));
						parentStack.pop();
						parentStack.pop();

					} else if (targetComponent == lastStep.c && targetPort == lastStep.p) {
						// noch kein beispiel:
						// wir suchen eine compositeComponent mit einem port, der auf den port des
						// parents gemappt ist

						// wenn wir keine eltern haben, dann kann hier auch nichts reinkommen
						if (hasParent) {
							Step s1 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
							ComponentPart temp2 = (ComponentPart) parentStack.pop();
							CompositeComponent temp = (CompositeComponent) parentStack.pop();
							Step s2 = new Step(copyStack(parentStack), parentCompositeComponentComponentPart, parentCompositeComponent,
									sourcePort, true);
							parentStack.push(temp);
							parentStack.push(temp2);
							if (isPathDuplicate(trace, s1, s2))
								continue;
							endOfPath = false;

							List<Step> newTrace = copyListAndAdd(trace, s1, s2);

							// we are moving one scope higher, so pop the stack (but remember my parent for
							// the next loop iteration)
							temp2 = (ComponentPart) parentStack.pop();
							temp = (CompositeComponent) parentStack.pop();
							allTraces.addAll(propagate(parentStack, temp, newTrace));
							parentStack.push(temp);
							parentStack.push(temp2);
						}
					}
				}
			} else {
				if (hasParent) {
					// trace is empty but we do have a parent -> this is a call on a nested
					// composite component
					// need to look on both my hierarchy and my children

					for (Port p : compositeComponent.getPorts()) { // myPorts

						if (p.getPortType() == PortType.IN || p.getPortType() == PortType.INOUT) {

							for (PortMapping portMapping : parentCompositeComponent.getPortMappings()) {
								Port sourcePort = portMapping.getSource();
								ComponentPart targetPart = portMapping.getTargetPart();
								Component targetComponent = targetPart.getComponent();
								Port targetPort = portMapping.getTarget().getPort();

								if (targetPart == compPart && targetComponent == compositeComponent
										&& targetPort == p) {
									// this mapping of my parent is to me *happy noises*

									Step s1 = new Step(copyStack(parentStack), targetPart, compositeComponent, targetPort, false);
									ComponentPart myPart = (ComponentPart) parentStack.pop();
									CompositeComponent parentComp = (CompositeComponent) parentStack.pop();
									Step s2 = new Step(copyStack(parentStack), null, parentCompositeComponent, sourcePort, true);
									parentStack.push(myPart);
									parentStack.push(parentComp);
									endOfPath = false;

									List<Step> newTrace = copyListAndAdd(trace, s1, s2);

									myPart = (ComponentPart) parentStack.pop();
									parentComp = (CompositeComponent) parentStack.pop();
									allTraces.addAll(propagate(parentStack, parentComp, newTrace));
									parentStack.push(myPart);
									parentStack.push(parentComp);
								}
							}

							for (PortConnector portConnector : parentCompositeComponent.getPortconnectors()) {
								ComponentPart sourcePart = portConnector.getSourcePart();
								Component sourceComponent = sourcePart.getComponent();
								Port sourcePort = portConnector.getSource().getPort();

								ComponentPart targetPart = portConnector.getTargetPart();
								Component targetComponent = targetPart.getComponent();
								Port targetPort = portConnector.getTarget().getPort();

								if (sourceComponent == compositeComponent && sourcePart == compPart
										&& sourcePort == p) {
									if (targetPort.getPortType() == PortType.IN) {
										continue;
									}
									Step s1 = new Step(copyStack(parentStack), sourcePart, sourceComponent, sourcePort, false);
									Object myPart = parentStack.pop();
									parentStack.push(targetPart);
									Step s2 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, true);
									parentStack.pop();
									parentStack.push(myPart);

									endOfPath = false;

									List<Step> newTrace = copyListAndAdd(trace, s1, s2);

									// keep the stack the same, as we are exploring components on the same hierarchy
									myPart = parentStack.pop();
									parentStack.push(targetPart);
									allTraces.addAll(propagate(parentStack, targetComponent, newTrace));
									parentStack.pop();
									parentStack.push(myPart);
								} else if (targetComponent == compositeComponent && targetPart == compPart
										&& targetPort == p) {
									if (sourcePort.getPortType() == PortType.IN) {
										continue;
									}

									Step s1 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
									Object myPart = parentStack.pop();
									parentStack.push(sourcePart);
									Step s2 = new Step(copyStack(parentStack), sourcePart, sourceComponent, sourcePort, true);
									parentStack.pop();
									parentStack.push(myPart);
									
									endOfPath = false;

									List<Step> newTrace = copyListAndAdd(trace, s1, s2);

									// keep the stack the same, as we are exploring components on the same hierarchy
									myPart = parentStack.pop();
									parentStack.push(sourcePart);
									allTraces.addAll(propagate(parentStack, sourceComponent, newTrace));
									parentStack.pop();
									parentStack.push(myPart);
								}
							}
						}

						if (p.getPortType() == PortType.OUT || p.getPortType() == PortType.INOUT) {

							for (PortMapping portMapping : compositeComponent.getPortMappings()) {
								Port sourcePort = portMapping.getSource();
								ComponentPart targetPart = portMapping.getTargetPart();
								Component targetComponent = targetPart.getComponent();
								Port targetPort = portMapping.getTarget().getPort();

								if (sourcePort == p) {
									Step s1 = new Step(copyStack(parentStack), compPart, compositeComponent, sourcePort, true);
									parentStack.push(compositeComponent);
									parentStack.push(targetPart);
									Step s2 = new Step(copyStack(parentStack), targetPart, targetComponent, targetPort, false);
									parentStack.pop();
									parentStack.pop();

									endOfPath = false;

									List<Step> newTrace = copyListAndAdd(trace, s1, s2);

									// ComponentPart myPart = (ComponentPart) parentStack.pop();
									parentStack.push(compositeComponent);
									parentStack.push(targetPart);
									//System.out.println(parentStack);
									allTraces.addAll(propagate(parentStack, targetComponent, newTrace));
									parentStack.pop();
									parentStack.pop();
									// parentStack.push(myPart);
								}
							}

						}
					}
				}
			}
		}

		if (endOfPath) {
			// no further connections/mappings were explored here, so the trace is completed
			// and we can stop the recursion
//			System.out.println("done:");
//			System.out.println(trace);
			if (!trace.isEmpty())
				allTraces.add(trace);
		}

		if (allTraces.isEmpty()) {
			// TODO: is this correct?
			// this component is a componentPart of a composite component, but has no
			// connections or mappings what so ever (just hanging around inside there)
			trace.add(new Step(copyStack(parentStack), compPart, c, null, true));
			allTraces.add(trace);
			return allTraces;
		}

		return allTraces;
	}

	/**
	 * This method checks whether the path described by the two steps has already
	 * been taken in the given trace i.e. if the two steps occur as direct neighbors
	 * in the list
	 * 
	 * @param trace A list of traces representing the current trace
	 * @param s1
	 * @param s2
	 * @return true if the path has already been taken
	 */
	private static boolean isPathDuplicate(List<Step> trace, Step s1, Step s2) {
		boolean found = false;
		// we don't care about the order TODO: should we?

		for (int i = 1; i < trace.size(); i++) {
			if (trace.get(i).equals(s1) && trace.get(i - 1).equals(s2))
				found = true;
			if (trace.get(i).equals(s2) && trace.get(i - 1).equals(s1))
				found = true;
		}

		return found;
	}

	/**
	 * This method shallow copies a list and adds two objects to the copy, which is
	 * then returned. First object a, then object b is added.
	 * 
	 * @param <T>  the generic type
	 * @param list list to be copied
	 * @param a    first object
	 * @param b    second object
	 * @return returns the copy including both objects
	 */
	private static <T> List<T> copyListAndAdd(List<T> list, T a, T b) {
		List<T> newList = new ArrayList<T>();
		newList.addAll(list);
		newList.add(a);
		newList.add(b);

		return newList;
	}

	private static Stack<Object> copyStack(Stack<Object> stack) {
		Stack<Object> result = new Stack<Object>();
		result.addAll(stack);
		return result;
	}

}