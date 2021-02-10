package de.uni_paderborn.swt.cards.dsl.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Sets;

import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Restriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.TmdslFactory;

public class RestrictionAssumptionResolver {
	public static Map<EObject, List<List<EObject>>> getAssumptionsForRestriction(Model model, SubMonitor monitor) {
		monitor.setTaskName("Calculating valid assumption sets for all restrictions");
		monitor.setWorkRemaining(100);
		Assumption assumption = model.getAssumption();
		

		Map<EObject, List<List<EObject>>> restrictionToAssumptionObject = new HashMap<EObject, List<List<EObject>>>();
		
		// need to copy restrictions from model, so the iterator does not break..
		List<EObject> resList = new ArrayList<EObject>();
		for (EObject res : model.getRestriction().getRestrictions()) {
			resList.add(res);
		}
		
		SubMonitor analyses = monitor.split(100);
		analyses.setTaskName("Run analysis");
		analyses.setWorkRemaining(resList.size());
		
		for (EObject res : resList) {
			SubMonitor mon = analyses.split(1);
			mon.setWorkRemaining(100);
			
			SubMonitor powerSet = mon.split(10);
			powerSet.setTaskName("Calculating assumption power set");
			List<List<EObject>> assumptionSet = getAssumptionSuperSet(assumption);
			powerSet.done();
			SubMonitor analysis = mon.split(90);
			
			Map<List<EObject>, List<RestrictionViolation>> assumptionsToViolations = new HashMap<List<EObject>, List<RestrictionViolation>>();
			Restriction restriction = TmdslFactory.eINSTANCE.createRestriction();
			restriction.getRestrictions().add(res);
			
			analysis.setWorkRemaining(assumptionSet.size());
			analysis.setTaskName("Running analysis");
			for (int i = 0; i < assumptionSet.size(); i ++) {
				analysis.worked(1);
				List<EObject> assumptionItem = assumptionSet.get(i);

				List<ComponentResult> results = Analyzer.analyzeModel(model, assumptionItem);
				List<RestrictionViolation> violations = RestrictionAnalyzer.checkRestrictions(restriction, results);
				assumptionsToViolations.put(assumptionItem, violations);
				
				if (violations.isEmpty()) {
					// remove all supersets of this set
					List<List<EObject>> toRemove = new ArrayList<List<EObject>>();
					for (int j = i+1; j < assumptionSet.size(); j++) {
						List<EObject> candidate = assumptionSet.get(j);

						if (assumptionsSuperset(assumptionItem, candidate)) {
							toRemove.add(candidate);
						}
					}
					assumptionSet.removeAll(toRemove);
					analysis.worked(toRemove.size());
				}
			}
			analysis.done();

			
			// the sets of the power set can have any order, so we clean 

			Set<List<EObject>> keys = assumptionsToViolations.keySet();
			List<List<EObject>> toRemove = new ArrayList<List<EObject>>();
			
			for (List<EObject> a : keys) {
				if (assumptionsToViolations.get(a).isEmpty()) {
					for (int i = 0; i < keys.size(); i++) {
						List<EObject> b = ((List<EObject>) keys.toArray()[i]);
						
						if (a == b) continue;

						if (assumptionsSuperset(a,b)) {
							toRemove.add(b);
						}
					}
					
				}
			}
			
			for (List<EObject> rem : toRemove) {
				assumptionsToViolations.remove(rem);
			}
			

			restrictionToAssumptionObject.put(res, new ArrayList<List<EObject>>());

			for (List<EObject> assumptionItem : assumptionsToViolations.keySet()) {
				boolean violated = assumptionsToViolations.get(assumptionItem).stream().anyMatch(e -> e.getViolatedRestriction().equals(res));
				// assumptions do satisfy restriction
				if (!violated) {
					restrictionToAssumptionObject.get(res).add(assumptionItem);
				}
			}
		}

		

		monitor.done();
		return restrictionToAssumptionObject;	
	}

	private static List<List<EObject>> getAssumptionSuperSet(Assumption assumption) {
		Set<EObject> set = new HashSet<EObject>();

		set.addAll(assumption.getComponentAssumptions());
		set.addAll(assumption.getComponentSanitzerAssumptions());
		set.addAll(assumption.getFlowAssumptions());
		set.addAll(assumption.getSanitizersAssumptions());
		set.addAll(assumption.getPortAssumptions());
		set.addAll(assumption.getPortSanitizerAssumptions());
		
		Set<Set<EObject>> powerSet =  Sets.powerSet(set);

		
		
		return powerSet.stream().map(e -> e.stream().collect(Collectors.toList())).collect(Collectors.toList());
	}
		
	/**
	 * returns whether b is superset of a, i.e. b has all assumptions that a has
	 * @param a
	 * @param b
	 */
	private static boolean assumptionsSuperset(List<EObject> a, List<EObject> b) {
		return b.containsAll(a);
	}
}
