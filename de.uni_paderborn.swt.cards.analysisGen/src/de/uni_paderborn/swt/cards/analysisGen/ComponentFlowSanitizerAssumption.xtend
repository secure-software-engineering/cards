package de.uni_paderborn.swt.cards.analysisGen

import de.uni_paderborn.swt.cards.mappingModel.ComponentMapping
import de.uni_paderborn.swt.cards.mappingModel.PortMapping
import de.uni_paderborn.swt.cards.mappingModel.SanitizerMapping

class ComponentFlowSanitizerAssumption {
	static def generate(
		String packageName, 
		String className, 
		ComponentMapping cMapping, 
		PortMapping sourceMapping, 
		PortMapping targetMapping, 
		SanitizerMapping sanitizerMapping,
		String assumptionId
	) {
		'''
		package «packageName»;		
		
		public class «className» {
			
			public static void main(String[] args) {
				
				System.out.println("Analysis not yet implemented");
				
			}

		}
		'''
	}
}