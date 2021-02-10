package de.uni_paderborn.swt.cards.dsl.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation;
import de.uni_paderborn.swt.cards.dsl.tmdsl.AllowGroupRestriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAllowRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroup;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupAllowRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPartAllowRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPartPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPreventRefinement;
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup;
import de.uni_paderborn.swt.cards.dsl.tmdsl.PreventGroupRestriction;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Restriction;

/**
 * 
 * @author Bastian Haverkamp
 *
 */
public class RestrictionAnalyzer {
	
	private RestrictionAnalyzer() {
		
	}

	/**
	 * Checks, whether the AssumptionAnalysis results conflict with the restrictions.
	 * @param restriction
	 * @param results
	 * @return
	 */
	public static List<RestrictionViolation> checkRestrictions(Restriction restriction, List<ComponentResult> results) {
		List<RestrictionViolation> violations = new ArrayList<RestrictionViolation>();
		List<AllowGroupRestriction> allowGroupRestrictions = restriction.getRestrictions().stream()
				.filter(r -> r instanceof AllowGroupRestriction).map(r -> (AllowGroupRestriction) r)
				.collect(Collectors.toList());
		List<PreventGroupRestriction> preventGroupRestrictions = restriction.getRestrictions().stream()
				.filter(r -> r instanceof PreventGroupRestriction).map(r -> (PreventGroupRestriction) r)
				.collect(Collectors.toList());

		// for every component..
		for (ComponentResult cr : results) {
			Stack<Object> stack = cr.getStack();
			// for every trace, we have a list of data types, that can reach the component
			// on this trace
			for (List<DataPoint> types : cr.getTypes()) {
				for (DataPoint dp : types) {

					// check if this data type is allowed here

					for (AllowGroupRestriction agr : allowGroupRestrictions) {
						violations.addAll(handleAllowGroupRestriction(agr, dp, stack));
					}

					for (PreventGroupRestriction pgr : preventGroupRestrictions) {
						violations.addAll(handlePreventGroupRestriction(pgr, dp, stack));
					}
				}

			}
		}

		return violations;
	}

	/**
	 * Checks, whether the DataPoint is legal in regards to this Restriction
	 * 
	 * @param pgr
	 * @param dp
	 * @param c
	 * @param cp
	 * @param cc
	 * @return
	 */
	private static List<RestrictionViolation> handlePreventGroupRestriction(PreventGroupRestriction pgr, DataPoint dp, Stack<Object> stack) {
		List<RestrictionViolation> violations = new ArrayList<RestrictionViolation>();
		List<ComponentGroupAllowRefinement> compGroupAllowRefinements = pgr.getCompGroupAllowRefinement();
		List<ComponentPartAllowRefinement> compPartAllowRefinements = pgr.getCompPartRefinement();
		List<ComponentAllowRefinement> compAllowRefinements = pgr.getCompAllowRefinement();
		boolean isAllowed = false;

		boolean isDataTypeApplicable = false;

		for (DataGroup dg : pgr.getGroupAsset()) {
			if (dg.getGroupedData().contains(dp.getType()))
				isDataTypeApplicable = true;
		}

		if (!isDataTypeApplicable) {
			// data type is not specified in this group, no violation possible here
			return violations;
		}

		// dataPoint is legal, if its applicable and at least one refinement allows it
		for (ComponentGroupAllowRefinement cgar : compGroupAllowRefinements) {
			isAllowed |= handleComponentGroupAllowRefinement(cgar, dp, stack);
		}

		for (ComponentPartAllowRefinement cpar : compPartAllowRefinements) {
			isAllowed |= handleComponentPartAllowRefinement(cpar, dp, stack);
		}

		for (ComponentAllowRefinement car : compAllowRefinements) {
			isAllowed |= handleComponentAllowRefinement(car, dp, stack);
		}

		if (!isAllowed) {
			// this dataPoint is not allowed, because the global prevent says so.
			violations.add(new RestrictionViolation(pgr, null, dp, stack));
		}

		return violations;
	}

	/**
	 * returns, whether the datapoint is legal in regards to the refinement
	 * 
	 * @param car
	 * @param dp
	 * @param c
	 * @return
	 */
	private static boolean handleComponentAllowRefinement(ComponentAllowRefinement car, DataPoint dp, Stack<Object> stack) {
		if (car.getDataAsset().contains(dp.getType())) {
			// this car refines the data type in question

			if (!Collections.disjoint(car.getComponent(), stack)) {
				// this car is about this data type and about the component
				if (car.getSanitizerOption() != null) {
					if (dp.getSanitizers().contains(car.getSanitizerOption().getSanitizer())) {
						return true;
					} else {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * returns, whether the datapoint is legal in regards to the refinement
	 * 
	 * @param cpar
	 * @param dp
	 * @param cp
	 * @return
	 */
	private static boolean handleComponentPartAllowRefinement(ComponentPartAllowRefinement cpar, DataPoint dp,
			Stack<Object> stack) {
		if (cpar.getDataAsset().contains(dp.getType())) {
			// this cppr refines the dataType in question

			if (!Collections.disjoint(cpar.getComponentPart(), stack)) {
				// this ccpr is about this data type and about this component part
				if (cpar.getSanitizerOption() != null) {
					if (dp.getSanitizers().contains(cpar.getSanitizerOption().getSanitizer())) {
						// we are sanitizing, no danger here
						return true;
					} else {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * returns whether the datapoint is legal in regards to the refinement
	 * 
	 * @param cgar
	 * @param dp
	 * @param c
	 * @return
	 */
	private static boolean handleComponentGroupAllowRefinement(ComponentGroupAllowRefinement cgar, DataPoint dp,
			Stack<Object> stack) {
		if (cgar.getDataAsset().contains(dp.getType())) {
			// this cgar refines the data type in question

			boolean isCgarInteresting = false;
			for (ComponentGroup cg : cgar.getComponentGroup()) {
				if (!Collections.disjoint(cg.getGroupedComponents(), stack)) {
					// this cgar is about the correct data type AND the correct component
					isCgarInteresting = true;
				}
			}

			if (isCgarInteresting) {
				boolean isCompExcluded = !Collections.disjoint(cgar.getExcludeComp(), stack);
				if (!isCompExcluded) {
					// this refinement says that this datatype is prevented from this component

					if (cgar.getSanitizerOption() != null) {
						if (dp.getSanitizers().contains(cgar.getSanitizerOption().getSanitizer())) {
							// calm down, the data was sanitized by a trusted sanitizer
							return true;
						} else {
							// Stop, you violated the law!
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks, whether the DataPoint is legal in regard to the AllowGroupRestriction
	 * 
	 * @param agr
	 * @param dp
	 * @param c
	 * @param cp
	 * @param cc
	 * @return
	 */
	private static List<RestrictionViolation> handleAllowGroupRestriction(AllowGroupRestriction agr, DataPoint dp, Stack<Object> stack) {
		List<RestrictionViolation> violations = new ArrayList<RestrictionViolation>();
		List<ComponentGroupPreventRefinement> compGroupPreventRefinements = agr.getCompGroupPreventRefinements();
		List<ComponentPartPreventRefinement> compPartPreventRefinements = agr.getCompPartPreventRefinements();
		List<ComponentPreventRefinement> compPreventRefinements = agr.getCompPreventRefinements();
		boolean isDataTypeApplicable = false;

		
		
		for (DataGroup dg : agr.getGroupAsset()) {
			if (dg.getGroupedData().contains(dp.getType()))
				isDataTypeApplicable = true;
		}

		if (!isDataTypeApplicable) {
			// data type is not specified in this group
			return violations;
		}

		
		// data is allowed in general, check for refinements now
		// data is legal, if its applicable and no refinement outrules it.
		for (ComponentGroupPreventRefinement cgpr : compGroupPreventRefinements) {
			boolean isLegalForRefinement = handleComponentGroupPreventRefinement(cgpr, dp, stack);

			if (!isLegalForRefinement) {
				violations.add(new RestrictionViolation(agr, cgpr, dp, stack));
			}

		}

		for (ComponentPartPreventRefinement cppr : compPartPreventRefinements) {
			boolean isLegalForRefinement = handleComponentPartPreventRefinement(cppr, dp, stack);

			if (!isLegalForRefinement) {
				violations.add(new RestrictionViolation(agr, cppr, dp, stack));
			}

		}

		for (ComponentPreventRefinement cpr : compPreventRefinements) {
			boolean isLegalForRefinement = handleComponentPreventRefinement(cpr, dp, stack);
			if (!isLegalForRefinement) {
				violations.add(new RestrictionViolation(agr, cpr, dp, stack));
			}	
			
		}

		return violations;
	}

	/**
	 * returns whether the DataPoint is legal in regards to this Refinement
	 * 
	 * @param cpr
	 * @param dp
	 * @param c
	 * @return
	 */
	private static boolean handleComponentPreventRefinement(ComponentPreventRefinement cpr, DataPoint dp, Stack<Object> stack) {
		if (cpr.getDataAsset().contains(dp.getType())) {
			// this cpr refines the data type in question
			if (!Collections.disjoint(cpr.getComponent(), stack)) {
				// this cpr is about this data type and about the component
				if (cpr.getSanitizerOption() != null) {
					if (dp.getSanitizers().contains(cpr.getSanitizerOption().getSanitizer())) {
						return true;
					} else {
						return false;
					}
				}
				// we are violating
				return false;
			}
		}
		return true;
	}

	/**
	 * returns whether the DataPoint is legal in regards to this Refinement
	 * 
	 * @param cppr
	 * @param dp
	 * @param cp
	 * @return
	 */
	private static boolean handleComponentPartPreventRefinement(ComponentPartPreventRefinement cppr, DataPoint dp,
			Stack<Object> stack) {
		if (cppr.getDataAsset().contains(dp.getType())) {
			// this cppr refines the dataType in question

			if (!Collections.disjoint(cppr.getComponentPart(), stack)) {
				// this ccpr is about this data type and about this component part
				if (cppr.getSanitizerOption() != null) {
					if (dp.getSanitizers().contains(cppr.getSanitizerOption().getSanitizer())) {
						// we are sanitizing, no danger here
						return true;
					} else {
						return false;
					}
				}
				// we are violating
				return false;
			}
		}
		return true;
	}

	/**
	 * returns whether the DataPoint is legal in regards to this Refinement
	 * 
	 * @param cgpr
	 * @param dp
	 * @param c
	 * @return
	 */
	private static boolean handleComponentGroupPreventRefinement(ComponentGroupPreventRefinement cgpr, DataPoint dp,
			Stack<Object> stack) {
		if (cgpr.getDataAsset().contains(dp.getType())) {
			// this cgpr refines the data type in question

			boolean isCgprInteresting = false;
			for (ComponentGroup cg : cgpr.getComponentGroup()) {
				if (!Collections.disjoint(cg.getGroupedComponents(), stack)) {
					// this cgpr is about the correct data type AND the correct component
					isCgprInteresting = true;
				}
			}

			if (isCgprInteresting) {
				boolean isCompExcluded = !Collections.disjoint(cgpr.getExcludeComp(), stack);
				if (!isCompExcluded) {
					// this refinement says that this datatype is prevented from this component

					if (cgpr.getSanitizerOption() != null) {
						if (dp.getSanitizers().contains(cgpr.getSanitizerOption().getSanitizer())) {
							// calm down, the data was sanitized by a trusted sanitizer
							return true;
						} else {
							return false;
						}
					}
					// Stop, you violated the law!
					return false;
				}
			}
		}
		return true;
	}
	
	
}
