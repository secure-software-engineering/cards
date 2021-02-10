package de.uni_paderborn.swt.cards.dsl.export

import java.util.List
import de.uni_paderborn.swt.cards.dsl.analyzer.data.RestrictionViolation
import java.util.ArrayList
import de.uni_paderborn.swt.cards.dsl.tmdsl.AllowGroupRestriction
import org.eclipse.emf.common.util.EList
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup
import de.uni_paderborn.swt.cards.dsl.tmdsl.PreventGroupRestriction
import org.eclipse.emf.ecore.EObject
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPreventRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupPreventRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPartPreventRefinement

class ViolationExporter {
	List<RestrictionViolation> violations;
	
	new (List<RestrictionViolation> violations) {
		this.violations = violations
	}
	
	def generate() {
		'''
		«generateHeader»
		«generateViolations(violations)»
		«generateFooter»
		'''
	}
	
	private def generateViolations(List<RestrictionViolation> violations) {
		var s = "";
		val vio = new ArrayList<RestrictionViolation>();
		vio.addAll(violations);
		while (vio.size > 0) {
			//group elements by groupRestriction
			val list = new ArrayList<RestrictionViolation>();
			val element = vio.get(0)
			vio.remove(0)
			list.add(element)
			
			for (rv : vio) {
				if (rv.violatedRestriction.equals(element.violatedRestriction)) {
					list.add(rv)
				}
			}
			vio.removeAll(list);
			s += generateViolationBlock(list);
		}
		
		return s;
	}
	
	private def generateViolationBlock(List<RestrictionViolation> violations) {
		if (violations.size > 0 && violations.get(0).violatedRestriction instanceof AllowGroupRestriction) {
			return generateViolationBlockAllow(violations);
		} else if (violations.size > 0 && violations.get(0).violatedRestriction instanceof PreventGroupRestriction){
			return generateViolationBlockPrevent(violations);
		} else {
			return "";
		}
	}
	
	/*
	 * called with violations for the same restriciton
	 */
	def generateViolationBlockAllow(List<RestrictionViolation> violations) {
		var s = '<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
          <div class="flex collapsible hover:bg-blue-200">
            <p class="text-xl flex-auto p-2 font-semibold">
              Violated AllowGroupRestriction
            </p>
            <p class="flex-right my-3">for</p>
            <div class="flex flex-wrap">
              '+ generateGroupAsset((violations.get(0).violatedRestriction as AllowGroupRestriction).groupAsset) +
            '</div>
          </div>
          <div class="pb-2 content">'

		while (violations.size > 0) {
			//group elements by groupRestriction
			val list = new ArrayList<RestrictionViolation>();
			val element = violations.get(0)
			violations.remove(0)
			list.add(element)
			
			for (rv : violations) {
				if (rv.violatedRefinement.equals(element.violatedRefinement)) {
					list.add(rv)
				}
			}
			violations.removeAll(list);
			s += generateRefinementBlock(list);
		}

          s+= '</div>          
        </div>'
        
        return s;
	}
	
	def generateRefinementBlock(ArrayList<RestrictionViolation> violations) {
		var s = '<div class="mb-2">
              <div class="p-2 flex collapsible hover:bg-blue-200 flex">
                  <div class="pl-4 mr-1">
                     <b>Violated Refinement</b> '+ printRefinement(violations.get(0).violatedRefinement) +
                  '</div>
              </div>
              <div class="pl-8 p-2 content">'

		s += generateViolationParagraphsAllow(violations)
		
        s += '</div>
		</div>'	
		return s
	}
	
	private def printRefinement(EObject object) {
		if (object instanceof ComponentPreventRefinement) {
			return RestrictionsExporter.generateCompRefinement(object)
		}
		if (object instanceof ComponentGroupPreventRefinement) {
			return RestrictionsExporter.generateCompGroupRefinement(object)
		}
		if (object instanceof ComponentPartPreventRefinement) {
			return RestrictionsExporter.generateCompPartRefinement(object)
		}
		return ""
	}
	
	/*
	 * called with violations for same restriction
	 */
	private def generateViolationBlockPrevent(List<RestrictionViolation> violations) {
		var s = '
        <div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
          <div class="flex collapsible hover:bg-blue-200">
            <p class="text-xl flex-auto p-2 font-semibold">
              Violated PreventGroupRestriction
            </p>
            <p class="flex-right my-3">for</p>
            <div class="flex flex-wrap">' + generateGroupAsset((violations.get(0).violatedRestriction as PreventGroupRestriction).groupAsset) +
            '</div>
          </div>
          <div class="pb-2 content">
            <div class="pl-8 p-2 content">' + generateViolationParagraphsPrevent(violations) +
            '</div>
          </div>          
        </div>		
		'
		return s;
	}
	
	/*
	 * called with violations for same restriction
	 */
	def generateViolationParagraphsPrevent(List<RestrictionViolation> violations) {
		var s = ""
		for (rv : violations) {
			s += "<p>"+ rv.dp.type.name + " at " + rv.stackDerivedName +"</p>"
		}
		return s;
	}
	
	def generateViolationParagraphsAllow(List<RestrictionViolation> violations) {
		var s = ""
		for (rv : violations) {
			s += "<p>"+ "at " + rv.stackDerivedName +"</p>"
		}
		return s;
	}
	
	private def generateGroupAsset(EList<DataGroup> list) {
		var s = ""
		for (dg : list) {
			s += '<p class="flex-right px-2 m-2 py-1 text-center bg-green-200 rounded-lg break-words">'
			s += dg.name
			s+= '</p>'
		}
		
		return s
	}
	
	private def generateFooter() {
		'''
	    	</div>
	    </div>
		'''
	}
	
	private def generateHeader() {
		'''
		<div id="Violation" class="flex items-center justify-center hidden">
			<div class="w-4/6 max-w-4xl items-center">
		        <h2 class="text-2xl text-center py-6 font-semibold">Restriction Violations</h2>
		'''
	}
	
}