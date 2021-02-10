package de.uni_paderborn.swt.cards.dsl.export

import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentAssumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentSanitizerAssumption
import java.util.ArrayList
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowAssumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentFlowSanitizationAssumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortSanitizerAssumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortAssumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.Model
import java.util.List
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataGroup

class AssumptionsExporter {
	val Assumption assumptions
	val List<DataType> dataTypes
	val List<DataGroup> dataGroups

	new(Model model) {
		dataGroups = model.groups.dataGroups
		dataTypes = model.system.datatypes
		this.assumptions = model.assumption
	}

	def generate() {
		'''
			«generateHTMLheader»
			«generateAssumptionsSortedByAssumptionKind»
			«generateAssumptionsSortedByComponent»
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
	
	

	private def generateHTMLheader() {
		'''
			<div id="Assumptions" class="flex items-center justify-center hidden">
		'''
	}

	private def generateHTMLFooter() {
		'''
			</div>
		'''
	}

	private def generateAssumptionsSortedByComponent() {
		'''
		<div id="Page2" class="w-4/6 max-w-4xl items-center hidden">
		        <div class="flex items-center justify-center">
		          <a
		            class="button mt-4 p-2 bg-blue-300 rounded-lg shadow-2xl hover:bg-blue-400"
		            href="#"
		            onclick="toggleShow()"
		            >Show assumptions sorted by assumption kind</a
		          >
		        </div>
		        «generateDataTypesAndDataGroups»
		        <div>
		          <h2 class="text-2xl text-center py-6 font-semibold">
		            All Assumptions sorted by Component
		          </h2>
				 «generateAssumptionsByComponent»
			 </div>
		</div>
		'''
	}

	private def generateAssumptionsSortedByAssumptionKind() {
		'''
			<div id="Page1" class="w-4/6 max-w-4xl items-center">
				<div class="flex items-center justify-center">
			    	<a
			        	class="button mt-4 p-2 bg-blue-300 rounded-lg shadow-2xl hover:bg-blue-400"
			            href="#"
			            onclick="toggleShow()"
			            >Show assumptions sorted by component</a>
			        </div>
			        «generateDataTypesAndDataGroups»
			        <div>
			          <h2 class="text-2xl text-center py-6 font-semibold">
			            All Assumptions sorted by AssumptionKind
			          </h2>
				«generateBlockHeader("Component Assumptions:")»
				
				«generateAllComponentAssumptions(assumptions)»
				
				«generateBlockFooter»
				
				«generateBlockHeader("Component Sanitizer Assumptions:")»
				
				«generateAllComponentSanitizerAssumptions(assumptions)»
				
				«generateBlockFooter»
				
				«generateBlockHeader("Flow Assumptions:")»
				
				«generateAllFlowAssumptions(assumptions)»
				
				«generateBlockFooter»
				
				«generateBlockHeader("Sanitization Assumptions:")»
				
				«generateAllSanitizationAssumptions(assumptions)»
				
				«generateBlockFooter»
				
				«generateBlockHeader("Port Assumptions:")»
				
				«generateAllPortAssumptions(assumptions)»
				
				«generateBlockFooter»
				
				«generateBlockHeader("Port Sanitization Assumptions:")»
				
				«generateAllPortSanitizationAssumptions(assumptions)»
				
				«generateBlockFooter»
				
				</div>
			</div>
		'''
	}
	
	def generateBlockFooter() {
		'''
            </div>
          </div>
		'''
	}
	
	def generateBlockHeader(String string) {
		'''
          <div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
            <h3
              class="text-xl py-2 pl-4 font-semibold collapsible hover:bg-blue-200"
            >
              «string»
            </h3>
            <div class="mb-6 p-2 content">
		'''
	}

	private def generateAssumptionsByComponent() {
		if(assumptions === null) return "";

		var returnCharSeq = "";
		val compAss = new ArrayList<ComponentAssumption>();
		compAss.addAll(assumptions.componentAssumptions);

		val compSanAss = new ArrayList<ComponentSanitizerAssumption>();
		compSanAss.addAll(assumptions.componentSanitzerAssumptions);

		val flowAss = new ArrayList<ComponentFlowAssumption>();
		flowAss.addAll(assumptions.flowAssumptions);

		val sanAss = new ArrayList<ComponentFlowSanitizationAssumption>();
		sanAss.addAll(assumptions.sanitizersAssumptions);

		val portAss = new ArrayList<PortAssumption>();
		portAss.addAll(assumptions.portAssumptions);

		val portSanAss = new ArrayList<PortSanitizerAssumption>();
		portSanAss.addAll(assumptions.portSanitizerAssumptions);

		var notDone = true;
		while (notDone) {

			var found = false;
			var Component comp = null;

			if (!compAss.isEmpty) {
				found = true;
				comp = compAss.get(0).comp;
			} else if (!compSanAss.isEmpty) {
				found = true;
				comp = compSanAss.get(0).sanitizerComp;
			} else if (!flowAss.isEmpty) {
				found = true;
				comp = flowAss.get(0).comp;
			} else if (!sanAss.isEmpty) {
				found = true;
				comp = sanAss.get(0).comp;
			}

			if (!found) {
				notDone = false;
			} else {
				returnCharSeq += generateComponentHeader(comp)
				for (var i = 0; i < compAss.size; i++) {
					if (compAss.get(i).comp.equals(comp)) {
						returnCharSeq += generateComponentAssumption(compAss.get(i))
						compAss.remove(i);
						i--;
					}
				}
				for (var i = 0; i < compSanAss.size; i++) {
					if (compSanAss.get(i).sanitizerComp.equals(comp)) {
						returnCharSeq += generateComponentSanitizerAssumption(compSanAss.get(i))
						compSanAss.remove(i);
						i--;
					}
				}
				for (var i = 0; i < flowAss.size; i++) {
					if (flowAss.get(i).comp.equals(comp)) {
						returnCharSeq += generateFlowAssumption(flowAss.get(i))
						flowAss.remove(i);
						i--;
					}
				}
				for (var i = 0; i < sanAss.size; i++) {
					if (sanAss.get(i).comp.equals(comp)) {
						returnCharSeq += generateSanitizationAssumption(sanAss.get(i))
						sanAss.remove(i);
						i--;
					}
				}
				for (var i = 0; i < portAss.size; i++) {
					if (portAss.get(i).port.eContainer.equals(comp)) {
						returnCharSeq += generatePortAssumption(portAss.get(i))
						portAss.remove(i);
						i--;
					}
				}
				for (var i = 0; i < portSanAss.size; i++) {
					if (portSanAss.get(i).sanitizerPort.eContainer.equals(comp)) {
						returnCharSeq += generatePortSanitizationAssumption(portSanAss.get(i))
						portSanAss.remove(i);
						i--;
					}
				}
				returnCharSeq += generateComponentFooter();
			}
		}

		return returnCharSeq
	}

	private def generateComponentHeader(Component c) {
		'''
		<div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
		            <h3
		              class="text-xl py-2 pl-4 font-semibold collapsible hover:bg-blue-200"
		            >
		              Component «c.name»:
		            </h3>
		            <div class="mb-6 p-2 content">
		'''
	}
	
	private def generateComponentFooter() {
		'''
            </div>
          </div>
		'''
	}

	private def generateSanitizationAssumptionsHeader() {
		'''
			<h3>Sanitization Assumptions:</h3>
		'''
	}

	private def generatePortAssumptionsHeader() {
		'''
			<h3>Port Assumptions:</h3>
		'''
	}

	private def generatePortSanitizationAssumptionsHeader() {
		'''
			<h3>Port Sanitization Assumptions:</h3>
		'''
	}

	private def generateAllPortSanitizationAssumptions(Assumption assumption) {
		if(assumption === null) return '';
		assumption.portSanitizerAssumptions.map[ass|generatePortSanitizationAssumption(ass)].reduce[a, b|a + "" + b]
	}

	private def generatePortSanitizationAssumption(PortSanitizerAssumption ass) {
		'''
		<p>Port «ass.sanitizerPort.name» sanitizes dataGroup «ass.sanitizedData.name» using «ass.sanitizer.name»</p>'''
	}

	private def generateAllPortAssumptions(Assumption assumption) {
		if(assumption === null) return '';
		assumption.portAssumptions.map[ass|generatePortAssumption(ass)].reduce[a, b|a + "" + b]
	}

	private def generatePortAssumption(PortAssumption ass) {
		'''
		<p>Port «ass.port.name» never outputs dataGroup «ass.data.name»</p>'''
	}

	private def generateAllSanitizationAssumptions(Assumption assumption) {
		if(assumption === null) return '';
		assumption.sanitizersAssumptions.map[ass|generateSanitizationAssumption(ass)].reduce[a, b|a + "" + b]
	}

	private def generateSanitizationAssumption(ComponentFlowSanitizationAssumption ass) {
		'''
		<p>Data«IF ass.data !== null» from group "«ass.data.name»",«ENDIF» that flows from «ass.source.name» to «ass.target.name», is always sanitized using «ass.sanitizer.name»</p>'''
	}

	private def generateAllFlowAssumptions(Assumption assumption) {
		if(assumption === null) return '';
		assumption.flowAssumptions.map[ass|generateFlowAssumption(ass)].reduce[a, b|a + "" + b]
	}

	private def generateFlowAssumption(ComponentFlowAssumption ass) {
		'''<p>Data«IF ass.data !== null » from group "«ass.data.name»",«ENDIF» never flows from «ass.source.name» to «ass.target.name»</p>'''
	}

	private def generateFlowAssumptionsHeader() {
		'''
			<h3>Flow Assumptions:</h3>
		'''
	}

	private def generateAllComponentSanitizerAssumptions(Assumption assumption) {
		if(assumption === null) return '';
		return assumption.componentSanitzerAssumptions.map[compSanAss|generateComponentSanitizerAssumption(compSanAss)].
			reduce[a, b|a + "" + b]
	}

	private def generateComponentSanitizerAssumption(ComponentSanitizerAssumption compSanAss) {
		'''<p>Component "«compSanAss.sanitizerComp.name»" sanitizes data from group "«compSanAss.sanitizedData.name»" using sanitizer "«compSanAss.sanitizer.name»"<p>'''
	}

	private def generateComponentSanitizerAssumptionsHeader() {
		'''
			<h3>Component Sanitizer Assumptions:</h3>
		'''
	}

	private def generateComponentAssumptionsHeader() {
		'''
          <div class="w-full mt-2 bg-gray-100 shadow-2xl rounded-lg">
            <h3
              class="text-xl py-2 pl-4 font-semibold collapsible hover:bg-blue-200"
            >
              Component Assumptions:
            </h3>
            <div class="mb-6 p-2 content">
		'''
	}

	private def generateAllComponentAssumptions(Assumption assumption) {
		if(assumption === null) return '';
		assumption.componentAssumptions.map[compAss|generateComponentAssumption(compAss)].reduce[a, b|a + "" + b]
	}

	private def generateComponentAssumption(ComponentAssumption compAss) {
		'''
			<p>Component "«compAss.comp.name»" never outputs data from group "«compAss.data.name»"</p>
		'''
	}

}
