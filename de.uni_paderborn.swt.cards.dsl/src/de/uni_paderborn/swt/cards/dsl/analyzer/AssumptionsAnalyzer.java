package de.uni_paderborn.swt.cards.dsl.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.Step;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Sanitizer;

public class AssumptionsAnalyzer {	
	
	protected static List<List<DataPoint>> analyzeDataTypes(Assumption assumption, List<List<Step>> traces) {
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
		
		return analyzeDataTypes(assumptions, traces);
	}
	
	/**
	 * This method returns all available data types for the given list of traces.
	 * A data type is available if and only if one of the components in the trace is sourceOf this data type <b>and</b> there is no assumption that stops propagation of the data type along the way.
	 * @param l
	 * @return
	 */
	protected static List<List<DataPoint>> analyzeDataTypes(List<EObject> assumptions, List<List<Step>> traces) {
		List<List<DataPoint>> dataTypes = new ArrayList<List<DataPoint>>();
		
		List<ComponentAssumption> componentAssumptions = assumptions.stream().filter(e -> e instanceof ComponentAssumption).map(e -> (ComponentAssumption) e).collect(Collectors.toList());
		List<ComponentSanitizerAssumption> componentSanitizerAssumptions = assumptions.stream().filter(e -> e instanceof ComponentSanitizerAssumption).map(e -> (ComponentSanitizerAssumption) e).collect(Collectors.toList());
		List<ComponentFlowAssumption> componentFlowAssumptions = assumptions.stream().filter(e -> e instanceof ComponentFlowAssumption).map(e -> (ComponentFlowAssumption) e).collect(Collectors.toList());
		List<ComponentFlowSanitizationAssumption> componentFlowSanitizationAssumption = assumptions.stream().filter(e -> e instanceof ComponentFlowSanitizationAssumption).map(e -> (ComponentFlowSanitizationAssumption) e).collect(Collectors.toList());
		List<PortAssumption> portAssumptions = assumptions.stream().filter(e -> e instanceof PortAssumption).map(e -> (PortAssumption) e).collect(Collectors.toList());
		List<PortSanitizerAssumption> portSanitizerAssumptions = assumptions.stream().filter(e -> e instanceof PortSanitizerAssumption).map(e -> (PortSanitizerAssumption) e).collect(Collectors.toList());
		
		for (List<Step> trace : traces) {
			List<DataPoint> types = new ArrayList<DataPoint>();
			for (int i = trace.size()-1; i >= 0; i--) {
				Step step = trace.get(i);
				
				if (step.c instanceof AtomicComponent) {
					
					for (DataType type : ((AtomicComponent) step.c).getSource4Data()) {
						DataPoint t = new DataPoint(type, null); 
						types.add(t);
					}
				}
							
				if (assumptions == null || assumptions.isEmpty()) continue;
							
				for (ComponentAssumption compAss : componentAssumptions) {
					if (compAss.getComp().equals(step.c) && step.isSource) {
						List<DataType> neverOutTypes = compAss.getData().getGroupedData();
						// this component can not pass the data on, so we remove it if its not the last one
						if(i > 0)
							types.removeIf(dp -> neverOutTypes.contains(dp.getType()));
					}
				}
				
				for (ComponentSanitizerAssumption compSanAss : componentSanitizerAssumptions) {
					if (compSanAss.getSanitizerComp().equals(step.c)) {
						DataGroup sanData = compSanAss.getSanitizedData();
						Sanitizer san = compSanAss.getSanitizer();
						types.stream()
							.filter((DataPoint dp) -> sanData.getGroupedData().contains(dp.getType()))
							.forEach(dp -> dp.getSanitizers().add(san));
					}
				}
				
				//flow assumptions
				if (i > 0) {
					for (ComponentFlowAssumption flowAss : componentFlowAssumptions) {
						if ((flowAss.getComp() == trace.get(i).c && flowAss.getSource() == step.p && flowAss.getTarget() == trace.get(i-1).p) || 
								(trace.size() > i+1 && flowAss.getComp() == trace.get(i).c && flowAss.getSource() == trace.get(i+1).p && flowAss.getTarget() == step.p)) {
							if (flowAss.getData() == null) {
								// this is a general assumption that no data will be transmitted on this connection, so we can throw everything away. 
								types = new ArrayList<DataPoint>();
							} else {
								// this is an assumption for specific data types
								types.removeIf(dp -> flowAss.getData().getGroupedData().contains(dp.getType()));
							}
						}
					}
					for (ComponentFlowSanitizationAssumption sanAss : componentFlowSanitizationAssumption) {
						if ((sanAss.getComp() == step.c && sanAss.getSource() == step.p && sanAss.getTarget() == trace.get(i-1).p) || 
								(trace.size() > i+1 && sanAss.getComp() == step.c && sanAss.getSource() == trace.get(i+1).p && sanAss.getTarget() == step.p)) {
							if (sanAss.getData() == null) {
								// this is a general assumption that all data will be sanitized on this connection
								types.forEach(dp -> {
									if(!dp.getSanitizers().contains(sanAss.getSanitizer())) 
										dp.getSanitizers().add(sanAss.getSanitizer());
								});
							} else {
								// this is an assumption for specific data types
								types.stream()
									.filter(dp -> sanAss.getData().getGroupedData().contains(dp.getType()))
									.forEach(dp -> { 
										if(!dp.getSanitizers().contains(sanAss.getSanitizer())) 
											dp.getSanitizers().add(sanAss.getSanitizer());
									});
							}
						}
					}
					
					// problem: check only ports that output on this trace, not those that input
					
					for (PortAssumption portAss : portAssumptions) {
						//only remove datatypes if this is the source port (it's neverOut, not neverIn!)
						if (portAss.getPort() == step.p && step.isSource) {
							if (portAss.getData() == null) {
								// this is a general assumption that no data will be transmitted on this connection, so we can throw everything away. 
								types = new ArrayList<DataPoint>();
							} else {
								// this is an assumption for specific data types
								types.removeIf(dp -> portAss.getData().getGroupedData().contains(dp.getType()));
							}
						}
					}
					
					for (PortSanitizerAssumption portSanAss : portSanitizerAssumptions) {
						if (portSanAss.getSanitizerPort() == step.p) {
							if (portSanAss.getSanitizedData() == null) {
								// this is a general assumption that all data will be sanitized on this connection
								types.forEach(dp -> dp.getSanitizers().add(portSanAss.getSanitizer()));
							} else {
								// this is an assumption for specific data types
								types.stream()
									.filter(dp -> portSanAss.getSanitizedData().getGroupedData().contains(dp.getType()))
									.forEach(dp -> dp.getSanitizers().add(portSanAss.getSanitizer()));
							}
						}						
					}
					
				}
				
				
				
			}
			dataTypes.add(types);
		}
		
		return dataTypes;
	}
}
