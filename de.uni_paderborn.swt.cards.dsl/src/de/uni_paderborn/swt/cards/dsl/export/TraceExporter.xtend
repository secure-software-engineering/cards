package de.uni_paderborn.swt.cards.dsl.export

import java.util.List
import de.uni_paderborn.swt.cards.dsl.tmdsl.Sanitizer
import de.uni_paderborn.swt.cards.dsl.analyzer.data.ComponentResult
import de.uni_paderborn.swt.cards.dsl.analyzer.data.Step
import de.uni_paderborn.swt.cards.dsl.analyzer.data.DataPoint

class TraceExporter {
		
	List<ComponentResult> data;
		
	new (List<ComponentResult> data) {
		this.data = data;
	}
	
	
	def generate() {
		'''
			{
				"components":[
					«generateComponents»	
				]
			}
		'''
	}
	
	private def generateComponents() {
		data.map[cr | generateComponent(cr)].reduce[a,b | a+", "+b];
	}
	
	private def generateComponent(ComponentResult cr){
		'''
		{
			"componentName":"«cr.stackDerivedName»",
			«generateTraces(cr.traces, cr.types)»
		}'''
	}
	
	
	private def generateTraces(List<List<Step>> traces, List<List<DataPoint>> types) {
		'''
		"traces":[
			«FOR trace : traces»
				«var i = traces.indexOf(trace)»
				{
					«generateTrace(trace)»,
					
					«generateDataTypes(types.get(i))»
				} «IF i != traces.size-1»,«ENDIF»
			«ENDFOR»
		]'''
	}
	
	private def generateTrace(List<Step> trace){
		'''
		"trace":[
			«trace.map[s | generateStep(s)].reduce[a,b| a+", "+b]»
		]'''
	}
	
	private def generateStep(Step s) {
		'''
		{
			«IF s.cp !== null»"componentPart":"«s.cp.name»",«ENDIF»
			"component":"«s.c.name»",
			«IF s.p !== null»"port":"«s.p.name»",«ENDIF»
			"isSource":"«s.isSource»"
		}'''
	}
	
	private def generateDataTypes(List<DataPoint> types){		
		'''
		"dataTypes":[
			«types.map[dp | generateDataPoint(dp)].reduce[a,b| a+", "+b]»
		]'''
	}
	
	private def generateDataPoint(DataPoint dp){
		'''
			{
				"type":"«dp.type.name»",
				«generateSanitizers(dp.sanitizers)»
			}
		'''
	}
	
	private def generateSanitizers(List<Sanitizer> sans) {
		'''
		"sanitizers":[
			«sans.map[san | generateSanitizer(san)].reduce[a,b| a+", "+b]»
		]'''
	}
	
	private def generateSanitizer(Sanitizer san) {
		'''
			"«san.name»"
		'''
	}
	
}
