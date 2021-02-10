package de.uni_paderborn.swt.cards.codeGen

import de.uni_paderborn.swt.cards.dsl.tmdsl.Assumption
import de.uni_paderborn.swt.cards.dsl.tmdsl.AtomicComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.ComponentPart
import de.uni_paderborn.swt.cards.dsl.tmdsl.CompositeComponent
import de.uni_paderborn.swt.cards.dsl.tmdsl.Port
import de.uni_paderborn.swt.cards.dsl.tmdsl.PortType

class ImplCompositeComponentGenerator {
	def generate(String packageName, String classesPackage, CompositeComponent cc, Assumption assumption) {
		val assumptionGenerator  = new AssumptionsGenerator(assumption);
		'''
		package «packageName»;
		
		import «classesPackage».Abstract«cc.name.toFirstUpper»_CC;
		import de.uni_paderborn.swt.cards.codeGen.library.annotations.assumptions.*;
		
		public class «cc.name.toFirstUpper»_CC extends Abstract«cc.name.toFirstUpper»_CC {
			
			«assumptionGenerator.generateComponentAssumption(cc)»
			@Override
			public void processComponent() {
				// TODO Auto-generated method stub
			}
			
			«FOR Port port : cc.ports»
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
		
			@Override
			public void initializeComponentParts() {
				// TODO Auto-generated method stub
				«FOR ComponentPart cp : cc.componentParts» 
					«IF cp.component instanceof AtomicComponent»
						this.«cp.name.toFirstLower» = new «cp.component.name.toFirstUpper»_AC();
					«ENDIF»
					«IF cp.component instanceof CompositeComponent»
						this.«cp.name.toFirstLower» = new «cp.component.name.toFirstUpper»_CC();
					«ENDIF»
				«ENDFOR»
			}
		
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