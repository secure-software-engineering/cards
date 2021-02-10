package de.uni_paderborn.swt.cards.codeGen

import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.DataType
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType

class ImplAtomicComponentGenerator {
	def generate(String packageName, String classesPackage, AtomicComponent ac, Assumption assumption) {
		val assumptionGenerator  = new AssumptionsGenerator(assumption);
		'''
		package «packageName»;
		
		
		import «classesPackage».Abstract«ac.name.toFirstUpper»_AC;
		import de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions.*;
		
		public class «ac.name.toFirstUpper»_AC extends Abstract«ac.name.toFirstUpper»_AC{
		
			«assumptionGenerator.generateComponentAssumption(ac)»
			«assumptionGenerator.generateComponentSanitizerAssumption(ac)»
			«assumptionGenerator.generateComponentFlowAssumption(ac)»
			«assumptionGenerator.generateComponentFlowSanitizationAssumption(ac)»
			«FOR Port p : ac.ports»
			«assumptionGenerator.generatePortAssumption(p)»
			«assumptionGenerator.generatePortSanitizerAssumption(p)»
			«ENDFOR»
			@Override
			public void doSomething() {
				// TODO Auto-generated method stub
				
			}
			
			«FOR Port port : ac.ports»
				«IF (port.portType == PortType.IN || port.portType == PortType.INOUT)»
					@Override
					public String readData«port.name.toFirstUpper»() {
						return super.readData«port.name.toFirstUpper»();
					}
					
				«ENDIF»
				«IF (port.portType == PortType.OUT || port.portType == PortType.INOUT)»
					@Override
					public void writeData«port.name.toFirstUpper»(String data) {
						super.writeData«port.name.toFirstUpper»(data);
					}
					
				«ENDIF»
			«ENDFOR»
			
			«FOR DataType d : ac.source4Data»
				@Override
				public String generate«d.name.toFirstUpper»() {
					// TODO Auto-generated method stub
					return null;
				}
				
			«ENDFOR»
			@Override
			protected void init() {
				// TODO Auto-generated method stub
				
			}
		
			@Override
			protected void beforeStop() {
				// TODO Auto-generated method stub
				
			}
		
		}
		'''
	}
}