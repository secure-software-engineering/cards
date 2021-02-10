package de.uni_paderborn.swt.cards.analysisGen

import de.uni_paderborn.swt.cards.mappingModel.ComponentMapping
import de.uni_paderborn.swt.cards.mappingModel.PortMapping
import de.uni_paderborn.swt.cards.mappingModel.SanitizerMapping
import java.util.List
import de.uni_paderborn.swt.cards.mappingModel.Mapping

class PortAssumption {
	static def generate(
		String packageName, 
		String className, 
		ComponentMapping cMapping, 
		List<Mapping> sourceMappings, 
		List<Mapping> targetMappings,
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