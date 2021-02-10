package de.uni_paderborn.swt.cards.codeGen

import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.Component
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port

class AssumptionsGenerator {
	
	var Assumption assumption;
	
	new(Assumption assumption) {
		this.assumption = assumption
	}
	
	
	def generateComponentAssumption(Component c) {
		assumption.componentAssumptions.filter[ass | ass.comp.equals(c)].map[ass | {
			"@ComponentAssumption(\"" + ass.data.name + "\")"
		}].reduce[p1, p2| p1 + "\n" + p2];
	}
	
	def generateComponentSanitizerAssumption(Component c) {
		assumption.componentSanitzerAssumptions.filter[ass | ass.sanitizerComp.equals(c)].map[ass | {
			"@ComponentSanitizerAssumption(dataGroup=\"" + ass.sanitizedData.name + "\", sanitizer=\"" + ass.sanitizer.name+ "\")"
		}].reduce[p1, p2| p1 + "\n" + p2];
	}

	def generateComponentFlowAssumption(Component c) {
		assumption.flowAssumptions.filter[ass | ass.comp.equals(c)].map[ass | {
			if (ass.data !== null) {
				"@ComponentFlowAssumption(dataGroup=\"" + ass.data.name + "\")"
			} else {
				"@ComponentFlowAssumption()"
			}
		}].reduce[p1, p2| p1 + "\n" + p2];
	}	
	
	def generateComponentFlowSanitizationAssumption(Component c) {
		assumption.sanitizersAssumptions.filter[ass | ass.comp.equals(c)].map[ass | {
			if (ass.data !== null) {
				"@ComponentFlowSanitizationAssumption(dataGroup=\"" + ass.data.name + "\", sanitizer=\"" + ass.sanitizer.name+ "\")"
			} else {
				"@ComponentFlowSanitizationAssumption(sanitizer=\"" + ass.sanitizer.name+ "\")"
			}
		}].reduce[p1, p2| p1 + "\n" + p2];
	}
	
	def generatePortAssumption(Port p) {
			assumption.portAssumptions.filter[ass | ass.port.equals(p)].map[ass | {
			"@PortAssumption(dataGroup=\"" + ass.data.name + "\")"
		}].reduce[p1, p2| p1 + "\n" + p2];
	}
	
	def generatePortSanitizerAssumption(Port p) {
			assumption.portSanitizerAssumptions.filter[ass | ass.sanitizerPort.equals(p)].map[ass | {
			"@PortSanitizerAssumption(dataGroup=\"" + ass.sanitizedData.name + "\", sanitizer=\"" + ass.sanitizer.name+ "\")"
		}].reduce[p1, p2| p1 + "\n" + p2];
	}
}