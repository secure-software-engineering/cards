package de.uni_paderborn.swt.cards.dsl.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import de.uni_paderborn.swt.cards.dsl.analyzer.Analyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.RestrictionAnalyzer;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult;
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation;
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model;

public class AnalysisSingleton {
	private List<RestrictionViolation> violations = null;
	private List<ComponentResult> analysisResults = null;
	private Integer oldModelHash = null;
	
	public List<RestrictionViolation> getViolations(Model model) {
		List<ComponentResult> analysis = getAnalysis(model);
		violations = RestrictionAnalyzer.checkRestrictions(model.getRestriction(), analysis);
		
		return violations;
	}
	
	public List<ComponentResult> getAnalysis(Model model) {
		List<EObject> children = new ArrayList<EObject>();
		model.eAllContents().forEachRemaining(element -> children.add(element));
		
		Integer newHash = children.hashCode();
		
		if (true) { //oldModelHash == null || !newHash.equals(oldModelHash)) {
			// recalculate analysis
			oldModelHash = newHash;
			analysisResults = Analyzer.analyzeModel(model);
		}
		
		return analysisResults;
	}	
}
