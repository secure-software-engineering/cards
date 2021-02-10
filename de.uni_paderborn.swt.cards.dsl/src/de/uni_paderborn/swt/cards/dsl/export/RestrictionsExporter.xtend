package de.uni_paderborn.swt.cards.dsl.export

import de.uni_paderborn.swt.cards.dsl.tmdsl.Restriction
import de.uni_paderborn.swt.cards.dsl.tmdsl.AllowGroupRestriction
import de.uni_paderborn.swt.cards.dsl.tmdsl.PreventGroupRestriction
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.common.util.EList
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupPreventRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroup
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component
import de.uni_paderborn.swt.cards.dsl.tmdsl.PreventRefinementSanitizerOption
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPartPreventRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPreventRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentGroupAllowRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.AllowRefinementSanitizerOption
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPartAllowRefinement
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAllowRefinement
import java.util.List
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model

class RestrictionsExporter {
	val Restriction restrictions
	val List<DataType> dataTypes
	val List<DataGroup> dataGroups
	val List<ComponentGroup> componentGroups

	new (Model model) {
		this.restrictions = model.restriction;
		dataGroups = model.groups.dataGroups
		componentGroups = model.groups.componentGroups
		dataTypes = model.system.datatypes
	}
	
	def generate() {
		'''
			«generateHTMLheader»
			«generateDataTypesAndDataGroups»
			«generateComponentGroups»
			«generateRestrictions»
			«generateHTMLFooter»
		'''
	}
	
		private def generateDataTypesAndDataGroups() {
		'''«generateDataTypes»
		«generateDataGroups»
		'''
	}
	
	private def generateDataTypes() {
		var s = '<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
          <h3 class="text-xl py-2 pl-4 font-semibold collapsible hover:bg-blue-200">
            DataTypes
          </h3>
			<div class="flex flex-wrap mb-6 p-2 content">'
		for (element : dataTypes) {
			s+='<p class="bg-blue-200 rounded-md m-2 p-2">' + element.name +"</p>"
		}
		s += "</div></div>"
	}
	
	private def generateDataGroups() {
		var s = ""
		for (dg : dataGroups) {
			s += generateDataGroup(dg)
		}
		return s
	}
	
	private def generateDataGroup(DataGroup group) {
		var s = '<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
          <div class="flex collapsible hover:bg-blue-200">
            <p class="text-xl flex-auto p-2 font-semibold">DataGroup</p>
            <p
              class="flex-right w-1/6 m-2 py-1 text-center bg-green-200 rounded-lg break-words"
            >'+ group.name +
   			'</p>
          </div>
          <div class="flex flex-wrap mb-6 p-2 content">'
		
		for (dt : group.groupedData) {
			s += '<p class="bg-blue-200 rounded-md m-2 p-2">' + dt.name + '</p>'
		}
		
		s += "</div></div>"
	}
	
	private def generateComponentGroups() {
		var s = ""
		for (cg : componentGroups) {
			s += generateComponentGroup(cg)
		}
		return s
	}
	
	private def generateComponentGroup(ComponentGroup group) {
		var s = '<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
          <div class="flex collapsible hover:bg-blue-200">
            <p class="text-xl flex-auto p-2 font-semibold">ComponentGroup</p>
            <p
              class="flex-right w-1/6 m-2 py-1 text-center bg-red-300 rounded-lg break-words"
            >'+ group.name +
   			'</p>
          </div>
          <div class="flex flex-wrap mb-6 p-2 content">'
		
		for (c : group.groupedComponents) {
			s += '<p class="bg-orange-300 rounded-md m-2 p-2">' + c.name + '</p>'
		}
		
		s += "</div></div>"
	}
	
	private def generateHTMLheader() {
		'''
		<div id="Restrictions" class="flex items-center justify-center">
		      <div class="w-4/6 max-w-4xl items-center">
		        <h2 class="text-2xl text-center py-6 font-semibold">Restrictions</h2>
		'''
	}
	
	private def generateHTMLFooter() {
		'''
			</div>
		</div>
		'''
	}
			
	private def generateRestrictions() {
		var s = ""
		s += generateAllowGroupRestrictions(restrictions.restrictions.filter[res | res instanceof AllowGroupRestriction])
		s += generatePreventGroupRestrictions(restrictions.restrictions.filter[res | res instanceof PreventGroupRestriction])
	}
	
	private def generateAllowGroupRestrictions(Iterable<EObject> list) {
		if (list === null || list.size == 0) return ""
		list.map[agr | generateAllowGroupRestriction(agr as AllowGroupRestriction)].reduce[a,b | a + "\n\n" + b];
	}
	
	private def generatePreventGroupRestrictions(Iterable<EObject> list) {
		if (list === null || list.size == 0) return ""
		list.map[pgr | generatePreventGroupRestriction(pgr as PreventGroupRestriction)].reduce[a,b | a + "\n\n" + b];
	}
	
	private def generatePreventGroupRestriction(PreventGroupRestriction pgr) {
		'''<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
		          <div class="flex collapsible hover:bg-blue-200">
		            <p class="text-xl flex-auto p-2 font-semibold">
		              PreventGroupRestriction
		            </p>
		            <p class="flex-right my-3">for</p>
		            <div class="flex flex-wrap">
						«generateGroupAsset(pgr.groupAsset)»
		            </div>
		          </div>
		          <div class="mb-6 p-2 content">
					«generateCompGroupAllowRefinements(pgr.compGroupAllowRefinement)»
					«generateCompPartAllowRefinements(pgr.compPartRefinement)»
					«generateCompAllowRefinements(pgr.compAllowRefinement)»
				  </div></div>'''
	}
	
	private def generateCompAllowRefinements(EList<ComponentAllowRefinement> list) {
		list.map[cgr | generateCompAllowRefinement(cgr)].reduce[a, b| a + "\n" + b]
	}
	
	private def generateCompAllowRefinement(ComponentAllowRefinement refinement) {
		'''
			<p>Allow «generateDataAsset(refinement.dataAsset)» to component«IF refinement.component.size > 1»s«ENDIF» «generateComponents(refinement.component)» «generatePreventSanitizerOption(refinement.sanitizerOption)»</p>
		'''
	}
	
	private def generateCompPartAllowRefinements(EList<ComponentPartAllowRefinement> list) {
		list.map[cgr | generateCompPartAllowRefinement(cgr)].reduce[a, b| a + "\n" + b]
	}
	
	private def generateCompPartAllowRefinement(ComponentPartAllowRefinement refinement) {
		'''
			<p>Allow «generateDataAsset(refinement.dataAsset)» to componentPart«IF refinement.componentPart.size > 1»s«ENDIF» «generateComponentPart(refinement.componentPart)» «generatePreventSanitizerOption(refinement.sanitizerOption)»</p>
		'''
	}
	
	private def generateCompGroupAllowRefinements(EList<ComponentGroupAllowRefinement> list) {
		list.map[cgr | generateCompGroupAllowRefinement(cgr)].reduce[a, b| a + "\n" + b]
	}
	
	private def generateCompGroupAllowRefinement(ComponentGroupAllowRefinement refinement) {
		'''
			<p>Allow «generateDataAsset(refinement.dataAsset)» to componentGroup«IF refinement.componentGroup.size > 1»s«ENDIF» «generateComponentGroup(refinement.componentGroup)» «generateExcludeComps(refinement.excludeComp)» «generatePreventSanitizerOption(refinement.sanitizerOption)»</p>
		'''
	}
	
	private def generatePreventSanitizerOption(AllowRefinementSanitizerOption option) {
		if (option === null) return ""
		'''
			if sanitized by «option.sanitizer.name»'''
	}
	
	private def generateAllowGroupRestriction(AllowGroupRestriction agr) {
		'''<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
          <div class="flex collapsible hover:bg-blue-200">
            <p class="text-xl flex-auto p-2 font-semibold">
              AllowGroupRestriction
            </p>
            <p class="flex-right my-3">for</p>
            <div class="flex flex-wrap">
				«generateGroupAsset(agr.groupAsset)»
            </div>
          </div>
          <div class="mb-6 p-2 content">
			«generateCompGroupRefinements(agr.compGroupPreventRefinements)»
			«generateCompPartRefinements(agr.compPartPreventRefinements)»
			«generateCompRefinements(agr.compPreventRefinements)»
		  </div></div>'''

	}
	
	private def generateCompRefinements(EList<ComponentPreventRefinement> list) {
		list.map[cgr | generateCompRefinement(cgr)].reduce[a, b| a + "\n" + b]
	}
	
	static protected def generateCompRefinement(ComponentPreventRefinement refinement) {
		'''
			<p>Prevent «generateDataAsset(refinement.dataAsset)» from component«IF refinement.component.size > 1»s«ENDIF» «generateComponents(refinement.component)» «generateSanitizerOption(refinement.sanitizerOption)»</p>
		'''
	}
	
	static private def generateComponents(EList<Component> list) {
		list.map[a | a.name].reduce[a,b | a+ ", " +b]
	}
	
	private def generateCompPartRefinements(EList<ComponentPartPreventRefinement> list) {
		list.map[cgr | generateCompPartRefinement(cgr)].reduce[a, b| a + "\n" + b]
	}
	
	static protected def generateCompPartRefinement(ComponentPartPreventRefinement refinement) {
		'''
			<p>Prevent «generateDataAsset(refinement.dataAsset)» from componentPart«IF refinement.componentPart.size > 1»s«ENDIF» «generateComponentPart(refinement.componentPart)» «generateSanitizerOption(refinement.sanitizerOption)»</p>
		'''
	}
	
	static private def generateComponentPart(EList<ComponentPart> list) {
		list.map[a | a.name].reduce[a,b | a+ ", " +b]
	}
	
	private def generateCompGroupRefinements(EList<ComponentGroupPreventRefinement> list) {
		list.map[cgr | generateCompGroupRefinement(cgr)].reduce[a, b| a + "\n" + b]
	}
	
	static protected def generateCompGroupRefinement(ComponentGroupPreventRefinement refinement) {
		'''
			<p>Prevent «generateDataAsset(refinement.dataAsset)» from componentGroup«IF refinement.componentGroup.size > 1»s«ENDIF» «generateComponentGroup(refinement.componentGroup)» «generateExcludeComps(refinement.excludeComp)» «generateSanitizerOption(refinement.sanitizerOption)»</p>
		'''
	}
	
	static private def generateDataAsset(EList<DataType> list) {
		return list.map[a | a.name].reduce[a,b | a + ", " + b]
	}
	
	static private def generateSanitizerOption(PreventRefinementSanitizerOption option) {
		if (option === null) return ""
		'''
			unless sanitized by «option.sanitizer.name»'''
	}
	
	static private def generateExcludeComps(EList<Component> list) {
		if (list === null || list.size == 0) return ""
		return "except " + list.map[a | a.name].reduce[a,b | a + ", " + b]
	}
	
	static private def generateComponentGroup(EList<ComponentGroup> list) {
		list.map[a | a.name].reduce[a,b | a+ ", " +b]
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
	
	
	
}